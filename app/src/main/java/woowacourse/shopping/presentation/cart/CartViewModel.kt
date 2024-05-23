package woowacourse.shopping.presentation.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.CartableProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.home.QuantityListener
import woowacourse.shopping.presentation.util.Event
import kotlin.concurrent.thread

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModel(), CartItemEventListener, QuantityListener {
    private var _currentPage: MutableLiveData<Int> = MutableLiveData(0)
    val currentPage: LiveData<Int>
        get() = _currentPage

    private val _cartableProducts: MutableLiveData<List<CartableProduct>> = MutableLiveData(emptyList())
    val cartableProducts: LiveData<List<CartableProduct>>
        get() = _cartableProducts

    private val _pageInformation: MutableLiveData<PageInformation> =
        MutableLiveData(PageInformation())
    val pageInformation: LiveData<PageInformation>
        get() = _pageInformation

    private var hasNext: Boolean = true

    init {
        thread {
            loadCurrentPageCartItems()
        }
    }

    fun loadPreviousPageCartItems() {
        thread {
            _currentPage.postValue(currentPage.value?.minus(1))
            loadCurrentPageCartItems()
        }
    }

    fun loadNextPageCartItems() {
        thread {
            _currentPage.postValue(currentPage.value?.plus(1))
            loadCurrentPageCartItems()
        }
    }

    fun loadCurrentPageCartItems() {
        val cartItems = cartRepository.fetchCartItems(currentPage.value ?: return)
        hasNext = cartRepository.fetchCartItems(currentPage.value?.plus(1) ?: return).isNotEmpty()
        setPageInformation()
        _cartableProducts.postValue(cartItems)
    }

    override fun onCartItemDelete(cartableProduct: CartableProduct) {
        cartableProduct.cartItem?.let {
            val cartItem = CartItem(
                it.id,
                cartableProduct.product.id,
                it.quantity
            )
            thread {
                cartRepository.removeCartItem(cartItem)
                if (cartableProducts.value?.size == 1 && currentPage.value != 0) {
                    _currentPage.postValue(currentPage.value?.minus(1))
                }
                loadCurrentPageCartItems()
            }
        }
    }

    private fun setPageInformation() {
        if (currentPage.value == 0) {
            _pageInformation.postValue(
                pageInformation.value?.copy(
                    previousPageEnabled = false,
                    nextPageEnabled = hasNext,
                )
            )
        } else {
            _pageInformation.postValue(
                pageInformation.value?.copy(
                    previousPageEnabled = true,
                    nextPageEnabled = hasNext,
                )
            )
        }
    }

    override fun onQuantityChange(productId: Long, quantity: Int) {
        if (quantity < 0) return
    }
}

data class PageInformation(
    val previousPageEnabled: Boolean = false,
    val nextPageEnabled: Boolean = false,
)
