package woowacourse.shopping.data.datasource

import io.kotest.assertions.any
import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.datasource.DefaultCart.addCartItem
import woowacourse.shopping.data.datasource.DefaultCart.deleteCartItem

class DefaultCartTest {
    @Test
    fun `장바구니에 상품을 추가할 수 있다`() {
        // when
        mockkObject(DefaultCart) {
            every { addCartItem(any(), any()) } returns 1
            val firstItemId = addCartItem(1, 1)
            assertThat(firstItemId).isEqualTo(1)
        }
    }

    @Test
    fun `장바구니에 추가한 상품을 제거할 수 있다`() {
        mockkObject(DefaultCart) {
            every { addCartItem(1, 1) } returns 1
            every { deleteCartItem(1) } returns 1
            addCartItem(1, 1)
            val removedItemId = deleteCartItem(1)
            assertThat(removedItemId).isEqualTo(1)
        }
    }
}
