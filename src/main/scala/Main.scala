import scala.math._		

object ScalaTour {
	def main(args: Array[String]): Unit = {
		println("===================== MULTIPLE PARAMETER LISTS (Currying) =====================")
		val numbers = List(1,2,3,4,5,6,7,8,9,10)
		val res = numbers.foldLeft(0)((m, n) => m + n)
		println(res)

		def foldLeft1[A,B](as: List[A], b0: B, op: (B, A) => B) = {}
		// below is bad type inference A is infered by numbers, B is infered by 0 but the compiler doesnt understand the rest
		// def notPossible = foldLeft1(numbers, 0, _+_)
		// do it like this instead
		def firstWay = foldLeft1[Int, Int](numbers, 0, _+_)
		def secondWay = foldLeft1(numbers, 0, (a: Int, b: Int) => a + b)

		// or use multiple parameter lists
		def foldLeft2[A,B](as: List[A], bs: B)(op: (B, A) => B) = {}
		//def secondWay = foldLeft2(numbers, 0)(_+_)

		// IMPLICIT PARAMETERS

		def execute(arg: Int)(implicit ec: scala.concurrent.ExecutionContext) = {}

		// PARTIAL APPLICATION
		// Call with only part of the arguments, get function in return

		val numberFunc = numbers.foldLeft(List[Int]()) _

		val squares = numberFunc((xs, x) => xs :+ x*x)
		println(squares)

		val cubes = numberFunc((xs, x) => xs :+ x*x*x)
		println(cubes)


		println("===================== CASE CLASSES =====================")
		// write 'case class' identifier and parameter list
		// Case CLass differences: Compact constructor, they are immutable, hashcode and equal (structural) defined on creation
		// Flexibility for pattern matching
		case class Book(isbn: String)

		val frankenstain = Book("934923874928374928374")
		println(frankenstain)

		// 'new' not needed case class has apply method for instantiation
		// parameters are initiated as val
		case class Message(sender: String, recipient: String, body: String)
		val message1 = Message("guillaume@quebec.ca", "jorge@catalonia.es", "Ça va ?")

		println(message1.sender)
		println(message1.body)
		println("respond >")

		// message1.sender = "travis@washington.us" fails because parameters are vals

		// COMPARISON
		// case classes are compared by structure and not by reference

		val message2 = Message("jorge@catalonia.es", "guillaume@quebec.ca", "Com va?")
		val message3 = Message("jorge@catalonia.es", "guillaume@quebec.ca", "Com va?")
		val messageAreTheSame = message2 == message3
		println(message2 == message1)
		println(messageAreTheSame)

		// COPYING
		// Can create shallow copy by using the copy function in case classes. Can modify constructor parameters if desired
		val message4 = Message("julien@bretagne.fr", "travis@washington.us", "Me zo o komz gant ma amezeg")
		val message5 = message4.copy(sender = message4.recipient, recipient = "claire@bourgogne.fr")
		println(message4)
		println(message5)
		println(message5.sender)
		println(message5.recipient)
		println(message5.body)

		val message6 = message4.copy()
		println(message6 == message4)

		println("===================== PATTERN MATCHING =====================")

		import scala.util.Random

		val x: Int = Random.nextInt(10)

		x match {
			case 0 => println("Zero")
			case 1 => println("One")
			case 2 => println("Two")
			case 3 => println("Three")
			case 4 => println("Four")
			case _ => println("Any")
		}

		println(x)

		// using as return statement of a function
		def matchTest(x: Int): String = {
			println("All of my love, for youuuuuu")
			x match {
				case 1 => "One"
				case 2 => "Two"
				case 3 => "Three"
				case 4 => "Four"
				case 5 => "Five"
				case _ => "Any"
			}
		}

		println(matchTest(x))
		println(matchTest(Random.nextInt(10)))
		println(matchTest(Random.nextInt(10)))

		// CASE CLASS MATCHING

		abstract class Notification
		case class Email1(sender: String, title: String, body: String) extends Notification
		case class SMS(caller: String, message: String) extends Notification
		case class VoiceRecording(contactName: String, link: String) extends Notification

		def showNotification(notification: Notification): String = {
			notification match {
				case Email1(sender, title, _) => 
					s"You got an email from $sender with title: $title"
				case SMS(number, message) =>
					s"You got an SMS from $number! Message: $message"
				case VoiceRecording(name, link) =>
					s"You received a Voice Recording from $name! Clickn $link to hear it"
			}
		}

		val someSms = SMS("38838874", "Where are you bro?")
		val someVoiceRecording = VoiceRecording("Rupert Murdoch", "https://click.me")

		println(showNotification(someSms))
		println(showNotification(someVoiceRecording))

		// PATTERN GUARDS

		def showImportantNotification(notification: Notification, importantPeopleInfo: Seq[String]): String = {
			notification match {
				case Email1(sender, _, _) if importantPeopleInfo.contains(sender) => 
					s"Email received from SPECIAL $sender!"
				case SMS(number, _) if importantPeopleInfo.contains(number) =>
					s"You got a SPECIAL SMS from $number!"
				case other =>
					showNotification(other)
			}
		}

		val importantPeopleInfo = Seq("865-5309", "jenny@gmail.com")

		val someSms2 = SMS("123-4567", "Are you there?")
		val someVoiceRecording2 = VoiceRecording("Tom", "voicerecording.org/id/123")
		val importantEmail = Email1("jenny@gmail.com", "Drinks tonight?", "I'm free after 5!")
		val importantSms = SMS("865-5309", "I'm here! Where are you dude?")

		println("=======================================")
		println(showImportantNotification(someSms2, importantPeopleInfo))
		println(showImportantNotification(someVoiceRecording2, importantPeopleInfo))
		println(showImportantNotification(importantEmail, importantPeopleInfo))
		println(showImportantNotification(importantSms, importantPeopleInfo))


		// You can match on the type only

		abstract class Device
		case class Phone(model: String) extends Device {
			def screenOff = "Turning screen off"
		}
		case class Computer(model: String) extends Device {
			def screenSaverOn = "Turning screen saver on..."
		}

		def goIdle(device: Device) = device match {
			case p: Phone => p.screenOff
			case c: Computer => c.screenSaverOn
		}

		val phone = Phone("fh33747")
		val computer = Computer("HDKK333")

		println(goIdle(phone))
		println(goIdle(computer))

		// remember the case identifiers p and c!

		// SEALED CLASS 
		// Traits and classes can be marked sealed which means all subtypes must be declared in the same file.
		// This assures that all subtypes are known

		abstract class Furniture
		case class Couch() extends Furniture
		case class Chair() extends Furniture

		def findPlaceToSit(piece: Furniture): String = piece match {
			case a: Couch => "Lie on the couch"
			case b: Chair => "Sit on the chair"
		}

		println(findPlaceToSit(Couch()))
		println(findPlaceToSit(Chair()))

		println("===================== SINGLETON OBJECTS =====================")
		
		 
		// DEFINING SINGLETON OBJECT
		object Box

		import logging.Logger.info
		
		info("Hello Worldito")

		class Project(name: String, daysToComplete: Int)

		class Test {
			val project1 = new Project("TPS Reports", 1)
			val project2 = new Project("Website redesign", 5)
			info("Created Projects")
		}

		// COMPANION OBJECT
		// Object with same name as that of a class. Companion object and classes can access private members of eachother

		//import scala.math._

		case class Circle(radius: Double) {
			import Circle._
			def area: Double = calculateArea(radius)
		}

		object Circle {
			private def calculateArea(radius: Double): Double = Pi * pow(radius, 2.0)
		}

		val circle1 = Circle(5.0)

		circle1.area
		
		println("Circle Area: ")
		println(circle1.area)

		// Using companion objects for factory methods

		

		// write class
		// write companion object with factory
		// use factory
		// write pattern matching to print emal

		class Email(val username: String, val domain: String)

		object Email {
			def fromEmail(email: String): Option[Email] = 
				email.split("@") match {
					case Array(a, b) => Some(new Email(a,b))
					case _ => None
				}
		}

		val someEmailInst = Email.fromEmail("granddady@autos.ml")

		someEmailInst match {
			case Some(email) => println(
			s"""Email is: 
				|Username: ${email.username}
				|Domain: ${email.domain}
			 """.stripMargin)
			 case None => println("Big mistake you stupid asshole. You think computers don't get tired?")
		}

		println("===================== REGULAR EXPRESSION PATTERS =====================")

		import scala.util.matching.Regex

		val numberPattern: Regex = "[0-9]".r

		numberPattern.findFirstMatchIn("9awesomepasseord123") match {
			case Some(_) => println("Password OK")
			case None => println("Password must contain a number")
		}


		println("==================")
		numberPattern.findFirstMatchIn("9awesomepassword123") match {
			case Some(a) => println(a)
		}
		println("==================")
		
		// SEARCHING FOR GROUPS
		val keyValPattern: Regex = "([0-9a-zA-Z- ]+): ([0-9a-zA-Z-#()/. ]+)".r

		val input: String = 
		"""background-color: #A03300;
		  |background-image: url(img/header100.png);
		  |background-position: top center;
		  |background-repeat: repeat-x;
		  |background-size: 2160x 108px;
		  |margin: 0
		  |height: 108px;
		  |width: 100%;""".stripMargin

		  for(patternMatch <- keyValPattern.findAllMatchIn(input))
		  	println(s"key: ${patternMatch.group(1)} value: ${patternMatch.group(2)}")

		 println("===================== EXTRACTOR OBJECT =====================")

		 object CustomerID {
		 
		 	def apply(name: String) = s"$name--${Random.nextLong}"
		 
		 	def unapply(customerID: String): Option[String] = {
		 		val stringArray: Array[String] = customerID.split("--")
		   	 	if(stringArray.tail.nonEmpty) Some(stringArray.head) else None
		   }
		 }
		 
		 val customer1ID = CustomerID("Sukyoung")  // Sukyoung--23098234908
		 customer1ID match {
		   case CustomerID(name) => println(name)  // prints Sukyoung
		   case _ => println("Could not extract a CustomerID")
		 }


		 val customer2ID = CustomerID("Nico")
		 val CustomerID(name) = customer2ID // Because the fucking CustomerID(name) syntax is equivalent to CustomerID.unapply(customer2ID): String
		 println(name)


		 println("===================== FOR COMPREHENSIONS =====================")

		 // for (enumerators) yield e
		 // 

		 case class User2(name: String, age: Int)

		 val userBase = List(
		 	User2("Travis", 28),
		 	User2("Kelly", 33),
		 	User2("Jennifer", 44),
		 	User2("Dennis", 23)
		 )

		 val twentySomethings = 
		 	for (user <- userBase if user.age >= 20 && user.age < 30) 
		 	yield user.name

		 twentySomethings.foreach(name => println(name))

		 // complicated example :(
		 def foo(n: Int, v: Int) =
		 	for (i <- 0 until n; j<- 0 until n if i + j == v)
		 		//println(s"$i,$j")
		 		yield (i, j)

		 foo(10, 10) foreach {
		 	case (i, j) => println(s"($i, $j)")
		 }

		 print("foooo22222")

		 def foo2(n: Int, v: Int) = 
		 for(i <- 0 until n; j <- 0 until n if i + j == v)
		 yield(i, j)

		 foo2(10, 10) foreach {
		 	case (i, j) => println(s"($i, $j)")
		 }

		 println("===================== GENERIC CLASSES =====================")

		 class Stack[A] {
	 	 	private var elements: List[A] = Nil
		 	def push(x: A) {elements = x :: elements}
		 	def peek: A = elements.head
		 	def pop(): A = {
		 		val currentTop = peek
		 		elements = elements.tail
		 		currentTop
		 	}
		 }

		 val stack = new Stack[Int]
		 stack.push(1)
		 stack.push(2)
		 stack.push(3)
		 println(stack.pop)
		 println(stack.pop)

		 class Fruit
		 class Apple extends Fruit
		 class Banana extends Fruit

		 val fstack = new Stack[Fruit]
		 val apple = new Apple
		 val banana = new Banana

		 fstack.push(apple)
		 fstack.push(banana)

		 println(fstack.pop)

		 println("===================== VARIANCES =====================")

		 class Foo[+A] // covariant class
		 class Bar[-A] // contravariant class
		 class Baz[A] // invariant class

		// COVARIANCE
		abstract class Animal2 {
			def name: String
		}
		case class Cat2(name: String) extends Animal2
		case class Dog2(name: String) extends Animal2
		 
		 // List in scala is declared as sealed abstract class List[+A] which means its type parameter is covariant

		 def printAnimalNames(animals: List[Animal2]): Unit = 
		 	animals.foreach {
		 		animal => println(animal.name)
		 	}

		 val cats: List[Cat2] = List(Cat2("Whiskers"), Cat2("Tom"))
		 val dogs: List[Dog2] = List(Dog2("Fido"), Dog2("Rex"))
		 val anims: List[Animal2] = List(Dog2("Dago"), Cat2("Berto"))

		printAnimalNames(cats)

		printAnimalNames(dogs)

		printAnimalNames(anims)

		// Contravariance
		abstract class Printer[-A] {
			def print(value: A): Unit
		}

		class AnimalPrinter extends Printer[Animal2] {
			def print(animal: Animal2): Unit =
				println("The animal's name is: " + animal.name)
		}

		class CatPrinter extends Printer[Cat2] {
			def print(cat: Cat2): Unit =
				println("The cat's name is: " + cat.name)
		}

		def printMyCat(printer: Printer[Cat2], cat: Cat2): Unit =
			printer.print(cat)

		val catPrinter: Printer[Cat2] = new CatPrinter
		val animalPrinter: Printer[Animal2] = new AnimalPrinter

		printMyCat(catPrinter, Cat2("Boots"))
		printMyCat(animalPrinter, Cat2("Boot"))

		// Invariance
		class Container2[A](value: A) {
			private var _value: A = value
			def getValue: A = _value
			def setValue(value: A): Unit = {
				_value = value
			}
		}

		val catContainer2: Container2[Cat2] = new Container2(Cat2("Felix"))
		//val animalContainer: Container[Animal] = catContainer
		//animalContainer.setValue(Dog("Spot"))
		// val cat: Cat = catContainer.getValue // Error because invariance

		// Further examples:
		abstract class SmallAnimal extends Animal
		case class Mouse(name: String) extends SmallAnimal

		// CONCLUSION: Variance is about relation of subtypes of parameter types.
		// Covariant: if A :> B (:< is subtype of) then class[A] >: class[B]
		// Contravariant: if A >: B then class[A] :< class[B]
		// Invariant: Given A >: B then class[A] and class[B] are unrelated

		println("===================== UPPER TYPE BOUNDS =====================")

		abstract class Animal {
			def name: String
		}

		abstract class Pet extends Animal {}

		class Cat extends Pet {
			override def name: String = "Cat"
		}		

		class Dog extends Pet {
			override def name: String = "Dog"
		}

		class Lion extends Animal {
			override def name: String = "Lion"
		}

		class PetContainer[P <: Pet](p: P) {
			def pet: P = p
		}

		val dogContainer = new PetContainer[Dog](new Dog) // respects subtype upper bound
		val catContainer = new PetContainer[Cat](new Cat) // ^^

		// val lionContainer = new PetContainer[Lion](new Lion) // Compiler ERROR, because of type bounds

		println("===================== LOWER TYPE BOUNDS =====================")

		// WRONG vvv function are contravariant in their parameters and covariant in their return types
		// trait Node[+B] {
			// def prepend(elem: B): Node[B]
		// }
// 
		// case class ListNode[+B](h: B, t: Node[B]) extends Node[B] {
			// def prepend(elem: B): ListNode[B] = ListNode(elem, this)
			// def head: B = h
			// def tail: Node[B] = t
		// }
// 
		// case class Nil[+B]() extends Node[B] {
			// def prepend(elem: B): ListNode[B] = ListNode(elem, this)
		// }

		trait Node[+B] {
			def prepend[U >: B](elem: U): Node[U]
		}

		case class ListNode[+B](h: B, t: Node[B]) extends Node[B] {
			def prepend[U >: B](elem: U): ListNode[U] = ListNode(elem, this)
			def head: B = h
			def tail: Node[B] = t
		}

		case class Nil2[+B]() extends Node[B] {
			def prepend[U >: B](elem: U): ListNode[U] = ListNode(elem, this)
		}

		// Now we can do the following
		trait Bird
		case class AfricanSwallow() extends Bird
		case class EuropeanSwallow() extends Bird

		val africanSwallowList = ListNode[AfricanSwallow](AfricanSwallow(), Nil2())
		val birdList: Node[Bird] = africanSwallowList
		// <-- notice that european swallows were added after african swallows
		birdList.prepend(EuropeanSwallow())

		println("===================== INNER CLASSES =====================")

		// In here only Node of the same instances can be linked together.
		// instantiate using graphInst.newNode.
		// class Graph {
			// class Node {
				// var connectedNodes: List[Node] = Nil
				// def connectTo(node: Node): Unit = {
					// if (!connectedNodes.exists(node.equals)) {
						// connectedNodes = node :: connectedNodes
					// }
				// }
			// }
			// var nodes: List[Node] = Nil
			// def newNode: Node = {
				// val res = new Node
				// nodes = res :: nodes
				// res
			// }
		// }

		class Graph {
			class Node {
				var connectedNodes: List[Graph#Node] = Nil
				def connectTo(node: Graph#Node): Unit = {
					if (!connectedNodes.exists(node.equals)) {
						connectedNodes = node :: connectedNodes
					}
				}
			}
			var nodes: List[Node] = Nil
			def newNode: Node = {
				val res = new Node
				nodes = res :: nodes
				res
			}
		}

		// Node is path dependent upon Graph
		// All nodes for an instance of graph can only be created through newNode of that instance

		val graph1: Graph = new Graph
		val node1: graph1.Node = graph1.newNode
		val node2: graph1.Node = graph1.newNode
		val node3: graph1.Node = graph1.newNode
		node1.connectTo(node2)
		node3.connectTo(node1)

		// Highly illegal. Police, open up wee, woo, wee, woo
		val graph2: Graph = new Graph
		val node21: graph2.Node = graph2.newNode
		val node22: graph2.Node = graph2.newNode
		node21.connectTo(node22)
		val graph3: Graph = new Graph
		val node31: graph3.Node = graph3.newNode
		node31.connectTo(node21) // breaking the law, again... // Only works using #

		// Adding # between the inner and outer class makes the inner class be able to 
		// be used in different instances. See example up above.
		
		println("===================== ABSTRACT TYPE MEMBERS =====================")

		trait Buffer {
			type T
			val element: T
		}

		abstract class SeqBuffer extends Buffer {
			type U
			type T <: Seq[U]
			def length = element.length
		}

		// Notice use of U to bound T to be subtype of Sequence of Us
		// Traits or classes with abstract type members can be used with anonymous classes

		abstract class IntSeqBuffer extends SeqBuffer {
			type U = Int
		}

		def newIntSeqBuf(elem1: Int, elem2: Int): IntSeqBuffer = 
			new IntSeqBuffer {
				type T = List[U]
				val element = List(elem1, elem2)
			}
		val buf = newIntSeqBuf(7, 8)
		println("length = " + buf.length)
		println("content = " + buf.element)

		// new IntSeqBuffer {} is the annonymous class lol

		// Only using type parameters
		abstract class Buffer2[+T] {
			val element: T
		}
		abstract class SeqBuffer2[U, +T <: Seq[U]] extends Buffer2[T] {
			def length = element.length
		}

		def newIntSeqBuf2(e1: Int, e2: Int): SeqBuffer2[Int, Seq[Int]] = 
			new SeqBuffer2[Int, List[Int]] {
				val element = List(e1, e2)
			}

		val buf2 = newIntSeqBuf2(7, 8)
		println("length = " + buf2.length)
		println("content = " + buf2.element)

		println("===================== COMPOUND TYPES =====================")

		trait Cloneable extends java.lang.Cloneable {
			override def clone(): Cloneable = {
				super.clone().asInstanceOf[Cloneable]
			}
		}
		trait Resetable {
			def reset: Unit
		}

		// notice 'with' in type of obj
		def cloneAndReset(obj: Cloneable with Resetable): Cloneable = {
			val cloned = obj.clone()
			obj.reset
			cloned
		}

		println("===================== SELF-TYPE =====================")

		trait User {
			def username: String
		}

		trait Tweeter {
			this: User => // reassign this
			def tweet(tweetText: String) = println(s"$username: $tweetText")
		}

		class VerifiedTweeter(val username_ : String) extends Tweeter with User {
			def username = s"real $username_"
		}

		val realBeyonce = new VerifiedTweeter("Beyonce")
		realBeyonce.tweet("Just spilled my glass of lemonade")

		// note how username is not extended into Tweeter, just it is in scope

		println("===================== IMPLICIT-PARAMETERS =====================")
		
		abstract class Monoid[A] {
			def add(x: A, y: A): A
			def unit: A
		}

		object ImplicitTest {
			implicit val stringMonoid: Monoid[String] = new Monoid[String] {
				def add(x: String, y: String): String = x concat y
				def unit: String = ""
			}

			implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
				def add(x: Int, y: Int): Int = x + y
				def unit: Int = 0
			}

			def sum[A](xs: List[A])(implicit m: Monoid[A]): A =
				if (xs.isEmpty) m.unit
				else m.add(xs.head, sum(xs.tail))

			def testImplicit(): Unit = {
 
				implicit val stringMonoid: Monoid[String] = new Monoid[String] {
					def add(x: String, y: String): String = y concat x
					def unit: String = " - oh noes - "
	 			}
				println(sum(List(1, 2, 3)))
				println(sum(List("uno", "dos", "tres")))
			}
		}

		val imp = ImplicitTest
		imp.testImplicit

		println("===================== IMPLICIT CONVERSIONS =====================")

		//import scala.language.implicitConversions

		implicit def list2ordered[A](x: List[A])(implicit elem2ordered: A => Ordered[A]): Ordered[List[A]] = 
			new Ordered[List[A]] {
				def compare(that: List[A]): Int = x.size - that.size
			}

		println(List(1, 2, 3) <= List(4, 5))

		// Int conversion implicit conversion in scala.Predef library
		import scala.language.implicitConversions

		implicit def int2Integer(x: Int) = 
			java.lang.Integer.valueOf(x)

		println("===================== POLYMORPHIC METHODS =====================")

		def listOfDuplicates[A](x: A, length: Int): List[A] = {
			if (length < 1)
				Nil
			else
				x :: listOfDuplicates(x, length - 1)
		}
		
		println(listOfDuplicates[Int](3, 4))
		println(listOfDuplicates("La", 8))

		println("===================== TYPE INFERENCE =====================")

		// Omitting the type
		val businessName = "Montreauz Jazz Cafe"		

		def squareOf(x: Int) = x * x

		def fac(n: Int): Int = if (n == 0) 1 else n * fac(n - 1)

		println(fac(8))

		// Polymorphic methods and generic classes can have types infered
		case class MyPair[A, B](x: A, y: B)
		val p = MyPair(1, "scala")

		def id[T](x: T) = x
		val q = id(1)

		// Parameters
		println(Seq(1, 3, 4).map(x => x * 2))

		// when not to rely on type inference

		var obj = null
		// obj = new AnyRef <-- cannot 

		println("===================== OPERATORS =====================")
		// In scala operators are methods
		println(10.+(1)) // + is equivalent to .+() method
		println(10 + 1)

		// HOW TO DEFINE OPERATORS
		case class Vec(x: Double, y: Double) {
			def +(that: Vec) = Vec(this.x + that.x, this.y + that.y)
		}

		val vector1 = Vec(1.0, 1.0)
		val vector2 = Vec(2.0, 2.0)

		val vector3 = vector1 + vector2
		println(vector3)

		// more complex expressions
		case class MyBool(x: Boolean) {
			def and(that: MyBool): MyBool = if (x) that else this
			def or(that: MyBool): MyBool = if (x) this else that
			def negate: MyBool = MyBool(!x)
		}

		// notice use of and and or as infix
		def not(x: MyBool) = x.negate
		def xor(x: MyBool, y: MyBool) = (x or y) and not(x and y)

		println("===================== BY-NAME PARAMETERS =====================")
		def calculate(input: => Int) = input * 37 // notice => to indicate by-name parameter

		def whileLoop(condition: => Boolean)(body: => Unit): Unit =
			if (condition) {
				body
				whileLoop(condition)(body)
			}

		var i = 32

		whileLoop(i > 0) {
			println(i)
			i -= 1
		}

		println("===================== ANNOTATIONS =========================")

		object DeprecationDemo extends App {
		  @deprecated("deprecation message", "release # which deprecates method")
		  def hello = "hola"
		  hello  
		}

		import scala.annotation.tailrec

		def factorial(x: Int): Int = {

			@tailrec
			def factorialHelper(x: Int, accumulator: Int): Int = {
				if (x == 1) accumulator else factorialHelper(x - 1, accumulator * x)
			}
			factorialHelper(x, 1)
		}

		// println(factorial(38))
// 
		// def factorialWrong(x: Int): Int = {
			// @tailrec
			// def factorialHelperWrong(x: Int): Int = {
				// if (x == 1) 1 else x*factorialHelperWrong(x - 1)
			// }
			// factorialHelperWrong(x)
		// }
// 
		// println(factorialWrong(38))
		// 
	}
}
