import scala.math._		

object ScalaTour {
	def main(args: Array[String]): Unit = {
		println("=== MULTIPLE PARAMETER LISTS (Currying) ===")
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


		println("=== CASE CLASSES ===")
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

		println("=== PATTERN MATCHING ===")

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

		println("=== SINGLETON OBJECTS ===")
		
		 
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

		println("=== REGULAR EXPRESSION PATTERS ===")

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

		 println("=== EXTRACTOR OBJECT ===")

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


		 println("=== FOR COMPREHENSIONS ===")

		 // for (enumerators) yield e
		 // 

		 case class User(name: String, age: Int)

		 val userBase = List(
		 	User("Travis", 28),
		 	User("Kelly", 33),
		 	User("Jennifer", 44),
		 	User("Dennis", 23)
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

		 println("=== GENERIC CLASSES ===")

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

		 print("=== VARIANCES ===")

		 class Foo[+A] // covariant class
		 class Bar[-A] // contravariant class
		 class Baz[A] // invariant class

		 
		 
	}
}
