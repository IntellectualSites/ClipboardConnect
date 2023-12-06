package net.onelitefeather.clipboardconnect.utils

import dev.derklaro.aerogel.Element
import dev.derklaro.aerogel.ElementMatcher
import dev.derklaro.aerogel.internal.reflect.TypeUtil

/**
 * The RawTypeMatcher class is used to match elements based on their raw type.
 *
 * @property requireElement The required element to be matched against.
 */
class RawTypeMatcher(private val requireElement: Element) : ElementMatcher {
    override fun test(element: Element): Boolean {
        return TypeUtil.rawType(element.componentType()) == requireElement.componentType()
    }
    companion object {
        fun <T> create(value: Class<T>): RawTypeMatcher {
            return RawTypeMatcher(Element.forType(value))
        }
    }
}