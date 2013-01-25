# ScalaEquals

ScalaEquals provides easy to use macros for generating correct equals/hashCode/canEqual implementations, 
never look up an equals/hashCode recipe again! The methods generated from ScalaEquals are taken directly 
from [Programming in Scala][pis] and strictly obey the contract of equals:

• *It is reflexive*: for any non-null value x, the expression x.equals(x) should return true.

• *It is symmetric*: for any non-null values x and y, x.equals(y) should return true 
if and only if y.equals(x) returns true.

• *It is transitive*: for any non-null values x, y, and z, if x.equals(y) re-turns true and 
y.equals(z) returns true, then x.equals(z) should return true.

• *It is consistent*: for any non-null values x and y, multiple invocations of x.equals(y) 
should consistently return true or consistently return false, provided no information used 
in equals comparisons on the objects is modified.

• For any non-null value x, x.equals(null) should return false.

Additionally, using `ScalaEquals.hash` will guarantee that `hashCode()` is always consistent with `equals`.
As a bonus, the macros also check that `equals`/`hashCode`/`canEqual` are defined correctly; 
they will catch misspellings, incorrect types, etc.

In the documentation and in this README, anywhere `ScalaEquals.equal` is seen it is assumed that 
`ScalaEquals.equalAllVals` also applies, unless otherwise stated. Additionally, in the documentation the
argument names are assumed to be `other`, but this is not a requirement, you may name the parameters
however you would like, the macros will find the name of the parameter and use it in the expansion.

## How To Use

### Equals Using All `val`s In Constructor
````scala
class Point(val x: Int, val y: Int) {
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
}
````
### Equals Using All `val`s In Constructor And Body
````scala
class Point(_x: Int, val y: Int) {
  val x: Int = _x
  override def equals(other: Any): Boolean = ScalaEquals.equalAllVals
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
}
````
### Equals Using User-Defined Fields
````scala
class Point(_x: Int, var y: Int) {
  def x: Int = _x
  override def equals(other: Any): Boolean = ScalaEquals.equal(x, y)
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
}
````

## Downloading

You may download ScalaEquals directly from [Sonatype][sona], or to use with sbt, add the
following to your project file:

```
libraryDependencies += "org.scalaequals" %% "scalaequals-core" % "1.0.3"
```

## Details

 - Every `equal` method will use `canEqual` if it is defined. 

 - Every `equal` method will use `super.equals(that)` if a super class that is not 
`AnyRef` or `Object` overrides `equals`.

 - `ScalaEquals.equal` will use all `val`s in constructor that are not inherited
from a parent class, i.e. `val`, `protected val`, `private val`, but not anything
qualified with `override`.

 - `ScalaEquals.equalAllVals` will use all `val`s in constructor AND body of class,
subject to the same constraints as above.

 - `ScalaEquals.equal(params)` will use *only* the fields specified in `params` (as well as
`super.equals` if applicable). Valid arguments include `val`, `var`, `lazy val`, 
and `def` that take no arguments. Any access modifier is allowed, and unlike `equal` 
and `equalAllVals`, arguments inherited from a super class and/or qualified with `override` 
may also be used.

 - `ScalaEquals.hash` will use `super.hashCode()` if and only if `super.equals(that)` is called 
in `equals`.

 - `ScalaEquals.canEquals` is a simple macro that converts to `other.isInstanceOf[Class]`

 - Works with classes, traits, abstract classes and generic variants (parameterized and
with abstract type members). As always, be careful about initialization order when using 
traits and abstract classes.

 - Requires Scala 2.10.0 and Java 1.7. Contact me if this is an issue.

## Feedback

### Questions or Comments?

Want to tell me how awesome (or horrible) ScalaEquals is? Send me an [email][asde]!

### Found a Bug? Want a New Feature?

Add issues or feature requests here on github at the [issue][] tracker, or better yet, 
fork the project and submit a pull request.

## Release Notes

View complete release notes [here][release].

## Testing

All implementations have been thoroughly tested using [`ScalaCheck`][check]. Check out 
the `core-test` project for details, specifically check out the documentation for 
[`EqualsFixture`][fixture] for exact testing methodology. If you find a problem, please
submit an [issue][]! As always, even when the implementation is perfect, it is good to
sanity check your own code to ensure that the logic of `equals`/`hashCode` is defined 
how you want it to be.

## Example Macro Expansion

````scala
class Point(val x: Int, val y: Int) {
  override def equals(other: Any): Boolean = ScalaEquals.equal
  override def hashCode(): Int = ScalaEquals.hash
  def canEqual(other: Any): Boolean = ScalaEquals.canEquals
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
  def canEqual(other: Any): Boolean = other.isInstanceOf[Point]
}
````

[fixture]: https://github.com/dicarlo2/ScalaEquals/blob/master/core-test/src/test/scala/org/scalaequals/test/EqualsFixture.scala
[issue]: https://github.com/dicarlo2/ScalaEquals/issues
[pis]: http://www.amazon.com/Programming-Scala-Comprehensive-Step-Step/dp/0981531644
[check]: https://github.com/rickynils/scalacheck
[asd]: https://github.com/dicarlo2
[asde]: alexdicarlo@gmail.com
[simple]: http://www.scala-sbt.org/
[sona]: http://oss.sonatype.org/content/repositories/releases/org/scalaequals/
[release]: https://github.com/dicarlo2/ScalaEquals/blob/master/RELEASE_NOTES.md
