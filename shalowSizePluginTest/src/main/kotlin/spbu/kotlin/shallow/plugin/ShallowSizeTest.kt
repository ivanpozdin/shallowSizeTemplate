package spbu.kotlin.shallow.plugin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

const val DEFAULT_SIZE = 8

// TODO: rewrite to parameterized tests
class AddShallowSizeMethodTest {
    companion object {
        @JvmStatic
        fun inputData() = listOf(
            Arguments.of(DEFAULT_SIZE, BaseClass("Hello").shallowSize()),
            Arguments.of(1, InternalClass(true).shallowSize()),
            Arguments.of(Int.SIZE_BYTES, InheritInterfaces(3).shallowSize()),
            Arguments.of(Int.SIZE_BYTES, InheritClass(3).shallowSize()),
            Arguments.of(2, NoBackField('c').shallowSize()),
            Arguments.of(Long.SIZE_BYTES + Int.SIZE_BYTES, PrivateFields(3).shallowSize()),
            Arguments.of(
                Byte.SIZE_BYTES + Short.SIZE_BYTES + Int.SIZE_BYTES + Long.SIZE_BYTES,
                MultipleFieldsInConstructor(1, 2, 3, 4).shallowSize()
            ),
            Arguments.of(4 * DEFAULT_SIZE, NullablePrimitives(1f, 1.0, 'c', true).shallowSize()),
            Arguments.of(DEFAULT_SIZE, JavaCharacter(Character('3')).shallowSize()),
            Arguments.of(Int.SIZE_BYTES + Long.SIZE_BYTES, NoExplicitType(3).shallowSize()),
            Arguments.of(Int.SIZE_BYTES, OverrideFieldFromClass(4).shallowSize()),
            Arguments.of(Int.SIZE_BYTES, OverrideFieldFromInterface(4).shallowSize()),
        )
    }

    @ParameterizedTest(name = "case {index}")
    @MethodSource("inputData")
    fun sumTest(expected: Int, real: Int) {
        assertEquals(expected, real)
    }
}
