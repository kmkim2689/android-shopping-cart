package woowacourse.shopping.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.databinding.ItemLoadMoreBinding
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.presentation.BindableAdapter
import java.lang.IllegalArgumentException

class ProductAdapter(
    private val homeItemClickListener: HomeItemEventListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), BindableAdapter<Product> {
    private var products: List<Product> = emptyList()
    private var loadStatus: LoadStatus = LoadStatus()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PRODUCT -> {
                val binding: ItemProductBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_product, parent, false)
                ProductViewHolder(binding, homeItemClickListener)
            }

            TYPE_LOAD -> {
                val binding: ItemLoadMoreBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_load_more, parent, false)
                LoadingViewHolder(binding, homeItemClickListener)
            }

            else -> throw IllegalArgumentException(EXCEPTION_ILLEGAL_VIEW_TYPE)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is ProductViewHolder -> holder.bind(products[position])
            is LoadingViewHolder -> holder.bind(loadStatus)
            else -> throw IllegalArgumentException(EXCEPTION_ILLEGAL_VIEW_TYPE)
        }
    }

    override fun getItemCount(): Int = products.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < products.size) {
            TYPE_PRODUCT
        } else {
            TYPE_LOAD
        }
    }

    override fun setData(data: List<Product>) {
        val previousSize = products.size
        products = data
        notifyItemRangeInserted(previousSize, data.size - previousSize)
    }

    fun updateLoadStatus(loadStatus: LoadStatus) {
        this.loadStatus = loadStatus
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        homeItemClickListener: HomeItemEventListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.homeItemClickListener = homeItemClickListener
        }

        fun bind(product: Product) {
            binding.product = product
        }
    }

    class LoadingViewHolder(
        private val binding: ItemLoadMoreBinding,
        homeItemClickListener: HomeItemEventListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.homeItemClickListener = homeItemClickListener
        }

        fun bind(loadStatus: LoadStatus) {
            binding.loadStatus = loadStatus
        }
    }

    companion object {
        const val TYPE_PRODUCT = 1000
        const val TYPE_LOAD = 1001
        private const val EXCEPTION_ILLEGAL_VIEW_TYPE = "유효하지 않은 뷰 타입입니다."
    }
}

@BindingAdapter("productThumbnail")
fun ImageView.setProductThumbnail(thumbnailUrl: String?) {
    Glide.with(context)
        .load(thumbnailUrl)
        .into(this)
}

@BindingAdapter("loadStatus")
fun Button.isLoadingButtonVisible(loadStatus: LoadStatus?) {
    if (loadStatus == null) return
    visibility =
        if (loadStatus.loadingAvailable && !loadStatus.isLoadingPage) View.VISIBLE else View.GONE
}
