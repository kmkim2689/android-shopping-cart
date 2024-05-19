package woowacourse.shopping.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.util.Event

class HomeViewModel(
    private val productRepository: ProductRepository,
) : ViewModel(), HomeItemClickListener {
    private var page: Int = 0

    private val _products: MutableLiveData<List<Product>> =
        MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>>
        get() = _products

    private val _loadStatus: MutableLiveData<LoadStatus> = MutableLiveData(LoadStatus())
    val loadStatus: LiveData<LoadStatus>
        get() = _loadStatus

    private val _navigateToDetailEvent: MutableLiveData<Event<Long>> = MutableLiveData()
    val navigateToDetailEvent: LiveData<Event<Long>>
        get() = _navigateToDetailEvent

    init {
        loadProducts()
    }

    fun loadProducts() {
        _loadStatus.value = loadStatus.value?.copy(isLoadingPage = true, loadingAvailable = false)
        _products.value = products.value?.plus(productRepository.fetchSinglePage(page++))

        products.value?.let {
            _loadStatus.value =
                loadStatus.value?.copy(
                    loadingAvailable = productRepository.fetchSinglePage(page).isNotEmpty(),
                    isLoadingPage = false,
                )
        }
    }

    override fun onProductItemClick(id: Long) {
        _navigateToDetailEvent.value = Event(id)
    }

    override fun onLoadClick() {
        loadProducts()
    }
}
