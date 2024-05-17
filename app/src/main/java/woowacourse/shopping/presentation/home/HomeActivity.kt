package woowacourse.shopping.presentation.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.DefaultProducts
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityHomeBinding
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.detail.DetailActivity

class HomeActivity : AppCompatActivity(), ProductItemClickListener, LoadClickListener {
    private val binding: ActivityHomeBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_home)
    }
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(
            this,
            HomeViewModelFactory(ProductRepositoryImpl(DefaultProducts)),
        )[HomeViewModel::class.java]
    }
    private val adapter: ProductAdapter by lazy {
        ProductAdapter(mutableListOf(), viewModel.loadStatus.value ?: LoadStatus(), this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.productAdapter = adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initializeProductListLayout()

        viewModel.loadProducts()
        viewModel.products.observe(this) {
            adapter.addProducts(it)
        }
        viewModel.loadStatus.observe(this) {
            adapter.updateLoadStatus(it)
        }

        setSupportActionBar(binding.toolbarHome)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_shopping_cart -> {
                startActivity(CartActivity.newIntent(this))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProductItemClick(id: Long) {
        startActivity(DetailActivity.newIntent(this, id))
    }

    override fun onLoadClick() {
        viewModel.loadProducts()
    }

    private fun initializeProductListLayout() {
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = ProductItemSpanSizeLookup(adapter)
        binding.rvHome.layoutManager = layoutManager
    }
}
