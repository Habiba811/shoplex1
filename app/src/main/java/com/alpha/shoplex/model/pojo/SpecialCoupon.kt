package com.alpha.shoplex.model.pojo

import com.alpha.shoplex.model.enumurations.DiscountType

class SpecialCoupon(
    val productName: String = "",
    discount: Float = 0F,
    discountType: DiscountType
) : SpecialDiscount(discount, discountType)