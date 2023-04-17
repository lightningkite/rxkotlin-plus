package com.lightningkite.rx.playbilling

import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ConnectionState
import com.android.billingclient.api.Purchase.PurchaseState
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.maybe.maybeOfEmpty
import com.badoo.reaktive.maybe.maybeOfError
import com.badoo.reaktive.observable.firstOrError
import com.badoo.reaktive.observable.mapNotNull
import com.badoo.reaktive.single.*
import com.badoo.reaktive.subject.publish.PublishSubject
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.playbilling.GoogleIab.toException
import com.lightningkite.rx.viewgenerators.ActivityAccess

object GoogleIab {
    val virtualPurchases: ArrayList<Purchase> = arrayListOf()
    fun addVirtualPurchase(id: String) {
        virtualPurchases.add(Purchase("""
            {
                "purchaseState": ${PurchaseState.PURCHASED},
                "quantity": 1,
                "purchaseTime": ${System.currentTimeMillis()},
                "packageName": "${staticApplicationContext.packageName}",
                "purchaseToken": "VIRTUAL",
                "autoRenewing": true,
                "productIds": ["$id"]
            }
        """.trimIndent(), ""))
    }
    private val onPurchaseUpdate: PublishSubject<Pair<BillingResult, List<Purchase>?>> = PublishSubject()
    private val c = BillingClient.newBuilder(staticApplicationContext).enablePendingPurchases().setListener { billingResult, purchases ->
        onPurchaseUpdate.onNext(billingResult to purchases)
    }.build()
    private var currentConnectionAttempt: Single<BillingClient>? = null
    private fun BillingResult.toException(): Exception = Exception("Error code ${responseCode}: $debugMessage")
    fun client(): Single<BillingClient> {
        if (c.connectionState == ConnectionState.CONNECTED) return singleOf(c)
        else {
            currentConnectionAttempt?.let { return it }
            val att = single <BillingClient> { em ->
                c.startConnection(object : BillingClientStateListener {
                    override fun onBillingServiceDisconnected() {
                        currentConnectionAttempt = null
                        em.onError(Exception("Billing disconnected"))
                    }

                    override fun onBillingSetupFinished(r: BillingResult) {
                        currentConnectionAttempt = null
                        when (r.responseCode) {
                            BillingResponseCode.OK -> em.onSuccess(c)
                            else -> {
                                em.onError(r.toException())
                            }
                        }
                    }
                })
            }
            return att
        }
    }

    fun refresh(
        type: String = BillingClient.ProductType.SUBS,
    ): Single<List<Purchase>> {
        return client().flatMap {
            single { em ->
                it.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(type).build()) { a, b ->
                    when (a.responseCode) {
                        BillingResponseCode.OK -> em.onSuccess(b)
                        else -> em.onError(a.toException())
                    }
                }
            }
        }.map { it + virtualPurchases }
    }

    fun queryProductDetails(request: List<QueryProductDetailsParams.Product>): Single<List<ProductDetails>> =
        client().flatMap {
            single { em ->
                it.queryProductDetailsAsync(
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(request)
                        .build()
                ) { a, b ->
                    when (a.responseCode) {
                        BillingResponseCode.OK -> em.onSuccess(b)
                        else -> em.onError(a.toException())
                    }
                }
            }
        }

    fun launchBillingFlow(
        dependency: ActivityAccess,
        account: String,
        sku: String,
        type: String = BillingClient.ProductType.SUBS,
    ): Maybe<Purchase> =
        queryProductDetails(
            listOf(
                QueryProductDetailsParams.Product.newBuilder().setProductId(sku).setProductType(type).build()
            )
        )
            .flatMapMaybe {
                launchBillingFlow(
                    dependency,
                    account,
                    it.first(),
                    it.first().subscriptionOfferDetails?.firstOrNull()?.offerToken
                )
            }

    fun launchBillingFlow(
        dependency: ActivityAccess,
        account: String,
        details: ProductDetails,
        offerToken: String? = null,
    ): Maybe<Purchase> {
        return launchBillingFlow(dependency, BillingFlowParams.newBuilder()
            .setObfuscatedAccountId(account)
            .setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .let {
                        offerToken?.let { t -> it.setOfferToken(t) }
                            ?: it
                    }
                    .build()
            ))
            .build(), setOf(details.productId))
    }

    fun launchBillingFlow(
        dependency: ActivityAccess,
        params: BillingFlowParams,
        products: Set<String>,
    ): Maybe<Purchase> {
        val r = c.launchBillingFlow(dependency.activity, params)

        return when (r.responseCode) {
            BillingResponseCode.OK -> onPurchaseUpdate
                .mapNotNull {
                it.second?.find { it.products.toSet() == products }?.let { p ->
                    it.first to p
                }
            }
                .firstOrError()
                .flatMapMaybe {
                when (it.first.responseCode) {
                    BillingResponseCode.OK -> maybeOf(it.second)
                    BillingResponseCode.USER_CANCELED -> maybeOfEmpty()
                    else -> maybeOfError(it.first.toException())
                }
            }

            else -> {
                return maybeOfError(r.toException())
            }
        }
    }
}