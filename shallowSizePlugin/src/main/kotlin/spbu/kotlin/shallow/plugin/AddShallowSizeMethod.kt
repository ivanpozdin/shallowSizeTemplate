package spbu.kotlin.shallow.plugin

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isBoolean
import org.jetbrains.kotlin.ir.types.isByte
import org.jetbrains.kotlin.ir.types.isChar
import org.jetbrains.kotlin.ir.types.isDouble
import org.jetbrains.kotlin.ir.types.isFloat
import org.jetbrains.kotlin.ir.types.isInt
import org.jetbrains.kotlin.ir.types.isLong
import org.jetbrains.kotlin.ir.types.isShort
import org.jetbrains.kotlin.ir.types.isUByte
import org.jetbrains.kotlin.ir.types.isULong
import org.jetbrains.kotlin.ir.types.isUShort
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties

const val DEFAULT_SIZE = 8
const val BOOLEAN_SIZE = 1
const val UNIT_SIZE = 8
const val SHALLOW_SIZE_FUNCTION_NAME = "shallowSize"

fun IrType.byteSize(): Int =
    when {
        this.isChar() -> Char.SIZE_BYTES
        this.isByte() -> Byte.SIZE_BYTES
        this.isShort() -> Short.SIZE_BYTES
        this.isInt() -> Int.SIZE_BYTES
        this.isLong() -> Long.SIZE_BYTES
        this.isUByte() -> UByte.SIZE_BYTES
        this.isUShort() -> UShort.SIZE_BYTES
        this.isULong() -> ULong.SIZE_BYTES
        this.isFloat() -> Float.SIZE_BYTES
        this.isDouble() -> Double.SIZE_BYTES
        this.isBoolean() -> BOOLEAN_SIZE
        this.isUnit() -> UNIT_SIZE
        else -> DEFAULT_SIZE
    }

fun IrClass.getShallowSizeFunc(): IrSimpleFunction {
    return functions.find { it.name.toString() == SHALLOW_SIZE_FUNCTION_NAME && it.valueParameters.isEmpty() }
        ?: throw NoSuchElementException("Function shallowSize is not found.")
}

fun IrClass.sumOfFields(): Int = this.properties.sumOf { it.backingField?.type?.byteSize() ?: 0 }

val Meta.GenerateShallowSize: CliPlugin
    get() = "Generate shallowSize method" {
        meta(
            classDeclaration(this, { element.isData() }) { declaration ->
                Transform.replace(
                    replacing = declaration.element,
                    newDeclaration = """
                        $`@annotations` $modality $visibility $kind $name $`(typeParameters)` $`(params)` $superTypes {
                        fun shallowSize(): Int = TODO("This function should be replaced at compile time")
                        $body
                        }
                        """.`class`
                )
            },
            irClass { clazz ->
                if (clazz.isData) {
                    val shallowSizeFunc = clazz.getShallowSizeFunc()
                    shallowSizeFunc.body = DeclarationIrBuilder(pluginContext, shallowSizeFunc.symbol).irBlockBody {
                        +irReturn(irInt(clazz.sumOfFields()))
                    }
                }
                clazz
            }
        )
    }
