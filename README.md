# ScalaEquals

ScalaEquals provides easy to use macros for generating correct equals/hashCode/canEquals implementations, 
never look up an equals/hashCode recipe again! The methods generated from ScalaEquals are taken directly 
from Programming in Scala* and strictly obey the contract of equals:

• *It is reflexive*: for any non-null value x, the expression x.equals(x) should return true.
• *It is symmetric*: for any non-null values x and y, x.equals(y) should return true 
if and only if y.equals(x) returns true.
• *It is transitive*: for any non-null values x, y, and z, if x.equals(y) re-turns true and 
y.equals(z) returns true, then x.equals(z) should return true.
• *It is consistent*: for any non-null values x and y, multiple invocations of x.equals(y) 
should consistently return true or consistently return false, provided no information used 
in equals comparisons on the objects is modified.
• For any non-null value x, x.equals(null) should return false.

Additionally, `ScalaEquals.hash` will guarantee that `hashCode()` is always consistent with `equals`.
In the documentation, anywhere `ScalaEquals.equal` is seen it is assumed that `ScalaEquals.equalAllVals` 
also applies, unless otherwise stated.

## How To Use

### Equals Using All `val`s In Constructor
````scala
class Point(val x: Int, val y: Int) {
  override def equals(other: Any): Boolean = ScalaEquals.equal(other)
  override def hashCode(): Int = ScalaEquals.hash
  def canEquals(other: Any): Boolean = ScalaEquals.canEquals(other)
}
````
### Equals Using All `val`s In Constructor And Body
````scala
class Point(_x: Int, val y: Int) {
  val x: Int = _x
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals(other)
  override def hashCode(): Int = ScalaEquals.hash
  def canEquals(other: Any): Boolean = ScalaEquals.canEquals(other)
}
````
### Equals Using User-Defined Fields
````scala
class Point(_x: Int, var y: Int) {
  def x: Int = _x
  override def equals(other: Any): Boolean = ScalaEquals.equal(other, x, y)
  override def hashCode(): Int = ScalaEquals.hash
  def canEquals(other: Any): Boolean = ScalaEquals.canEquals(other)
}
````

## Downloading

Feel free to fork (or checkout) this repo. Additionally, packaged jars are available
in the root directory.

## Details

 - Every `equal` method will use `canEqual` if it is defined. Every `equal` method
will use `super.equals(that)` if a super class that is not `AnyRef` or `Object` 
overrides `equals`. 

 - `ScalaEquals.equal` will use all `val`s in constructor that are not inherited
from a parent class, i.e. `val`, `protected val`, `private val`, but not anything
qualified with `override`.

 - `ScalaEquals.equalAllVals` will use all `val`s in constructor AND body of class,
subject to the same constraints as above.

 - `ScalaEquals.equal(params)` will use ONLY the fields specified in `params`. Valid
arguments include `val`, `var`, `lazy val`, and `def` that take no arguments. Any access
modifier is allowed, and unlike `equal` and `equalAllVals`, arguments qualified with 
`override` may also be used.

 - `ScalaEquals.hash` *MUST* be used in conjunction with a `ScalaEquals.equal` method and
the definition of `hashCode()` *MUST* come after the definition of `equals` in the file.

 - `ScalaEquals.hash` will call `super.hashCode()` if `super.equals(that)` is called in
`equals`.

 - `ScalaEquals.canEquals` is a simple macro that converts to `other.isInstanceOf[Class]`

 - Works with classes, traits, abstract classes and generic variants (parameterized and
with abstract type members). As always, be careful about initialization order when using 
traits and abstract classes.

## Testing

All implementations have been thoroughly tested using `ScalaCheck`. Feel free to check
the `core-test` project for specific details, specifically check out the documentation
for [`EqualsFixture`][fixture] for exact testing methodology. If you find a problem, please
submit an [issue][]! As always, even if the implementation is perfect, it is good to
sanity check your own code to ensure that the logic of `equals` is defined how you want it
to be.

## Example Macro Expansion

````scala
class Point(val x: Int, val y: Int) {
  override def equals(other: Any): Boolean = ScalaEquals.equal(other)
  override def hashCode(): Int = ScalaEquals.hash
  def canEquals(other: Any): Boolean = ScalaEquals.canEquals(other)
}
````
becomes
````scala
class Point(val x: Int, val y: Int) {
  override def equals(other: Any): Boolean = other match {
    case that: Point => (that canEqual this) && that.x == this.x && that.y == this.y
    case _ => false
  }
  override def hashCode(): Int = Objects.hash(Seq(x, y))
  def canEquals(other: Any): Boolean = other.isInstanceOf[Point]
}
````

[fixture]: https://github.com/dicarlo2/ScalaEquals/blob/master/core-test/src/test/scala/org/scalaequals/test/EqualsFixture.scala
[issue]: https://github.com/dicarlo2/ScalaEquals/issues
