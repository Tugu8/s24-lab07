# s24-rec06

## Lab 7 - Refactoring ба Anti-pattern Мэргэжлийн Тайлан

Энэхүү тайлан нь `frogger` болон `drawing` package-ууд дээрх муу дизайн, анти-паттерн, тэдгээрээс үүсэх эрсдэл, хэрэгжүүлсэн рефакторинг, мөн шинэ зохион байгуулалт яг юу хийж байгааг мэргэжлийн түвшинд тайлбарласан болно. Тайлангийн гол зорилго нь кодын гадаад behavior-ийг аль болох хэвээр үлдээж, дотоод бүтэц, responsibility-ийн хуваарилалт, maintainability, readability, extensibility-г сайжруулсан эсэхийг баримтжуулах юм.

---

## 1. Тайлангийн зорилго

Энэ лабораторийн хүрээнд дараах зорилтыг хэрэгжүүлсэн.

1. Хуучин кодын доторх анти-паттерн болон design smell-үүдийг тодорхойлох.
2. Яагаад тухайн бүтэц нь муу дизайн болохыг тайлбарлах.
3. Ямар рефакторинг хийх нь зөв болохыг сонгох.
4. Frogger хэсэг дээр бодит кодын өөрчлөлт хийх.
5. Drawing хэсэг дээр код өөрчлөхгүйгээр мэргэжлийн шинжилгээ өгөх.

---

## 2. Хамрах хүрээ

Энэ тайлан дараах файлуудыг хамарна.

1. `Java/src/main/java/frogger/Frogger.java`
2. `Java/src/main/java/frogger/Road.java`
3. `Java/src/main/java/frogger/Records.java`
4. `Java/src/main/java/frogger/FroggerID.java`
5. `Java/src/main/java/drawing/Drawing.java`
6. `Java/src/main/java/drawing/shapes/Shape.java`

---

## 3. Ашигласан үндсэн рефакторинг зарчим

Энэ ажлын үеэр дараах software design зарчмуудыг баримталсан.

1. `Single Responsibility Principle (SRP)`
Нэг class нэг үндсэн responsibility хариуцах ёстой. Замын дүрэм, frogger-ийн төлөв, бүртгэлийн логик, rendering логик нь тус тусдаа responsibility байх ёстой.

2. `Encapsulation`
Class-ийн дотоод өгөгдлийн бүтэц гаднаас шууд мэдэгдэх ёсгүй. Өгөгдлийг нууж, behavior-оор дамжуулан ажиллах нь илүү зөв.

3. `Tell, Don't Ask`
Object-оос дотоод өгөгдлийг нь аваад гаднаас шийдвэр гаргахын оронд тухайн object өөрөө шийдвэр гаргах ёстой.

4. `Value Object`
Нэг бүхэл утга, нэг ойлголтыг хэд хэдэн primitive field-ээр тараахын оронд нэг утгын объект болгон багцлах нь илүү найдвартай.

5. `Low Coupling, High Cohesion`
Class-ууд хоорондоо хэт уялдахгүй, харин тус тусынхаа responsibility-д төвлөрсөн байх ёстой.

---

## 4. Даалгавар 1 - Frogger + Road

### 4.1 Рефактороос өмнөх кодын байдал

Рефактороос өмнө `Road` class нь зөвхөн `occupied` массив хадгалж, түүнийг getter-ээр буцаадаг маш нимгэн data holder байсан.

```java
public class Road {
	private final boolean[] occupied;

	public Road(boolean[] occupied) {
		this.occupied = occupied;
	}

	public boolean[] getOccupied() {
		return this.occupied;
	}
}
```

Харин `Frogger` class нь замын дотоод өгөгдлийг авч, замын дүрэмтэй холбоотой шийдвэрийг өөрөө гаргаж байсан.

```java
public boolean isOccupied(int position) {
	boolean[] occupied = this.road.getOccupied();
	return occupied[position];
}

public boolean isValid(int position) {
	if (position < 0) return false;
	boolean[] occupied = this.road.getOccupied();
	return position < occupied.length;
}
```

Мөн `move()` method нь дараагийн байрлал руу очиж болох эсэхийг Frogger талдаа шалгаж байсан.

```java
public boolean move(boolean forward) {
	int nextPosition = this.position + (forward ? 1 : -1);
	if (!isValid(nextPosition) || isOccupied(nextPosition)) {
		return false;
	}
	this.position = nextPosition;
	return true;
}
```

### 4.2 Илэрсэн анти-паттерн ба design smell

| Ангилал | Хаана илэрсэн | Мэргэжлийн тайлбар |
| --- | --- | --- |
| Feature Envy | `Frogger` дотор | `Frogger` нь `Road`-ийн өгөгдөл дээр тулгуурлан замын дүрмийг өөрөө хэрэгжүүлж байсан. Энэ нь өөр class-ийн behavior-ийг булаан авч байгаа хэлбэр юм. |
| Inappropriate Intimacy | `Frogger` ба `Road` хооронд | `Frogger` нь `Road`-ийн дотоод `boolean[] occupied` массивыг мэддэг, түүнд тулгуурлан шийдвэр гаргадаг байсан. |
| Encapsulation violation | `Road.getOccupied()` | `Road` өөрийн дотоод өгөгдлийн төлөөллийг ил болгож байсан. Domain API өгөхийн оронд implementation detail-ээ гаргаж өгсөн. |
| Low Cohesion | `Frogger` дотор | `Frogger` нь өөрийн position-оос гадна замын шалгалтын логик давхар хариуцаж байсан. |

### 4.3 Хуучин кодоос үүсэх бодит асуудал

1. `Frogger` нь замын өгөгдөл `boolean[]` гэдгийг мэддэг байсан. Хэрэв дараа нь `Road` доторх өгөгдлийн бүтэц өөрчлөгдвөл `Frogger`-ийг заавал хамт өөрчлөх шаардлагатай болно.

2. Замын дүрэм нэг цэгт төвлөрөөгүй байсан. Validation, occupancy check, move decision зэрэг логик хоёр class-ийн дунд тарамдсан байсан.

3. `Road` нь domain object гэхээсээ илүү зөвхөн өгөгдөл хадгалагч болж хувирсан. Энэ нь object-oriented загварын хүчийг ашиглаагүй гэсэн үг.

4. Код унших үед “замын талаархи бүх логик хаана байна вэ?” гэсэн асуултын хариулт тодорхой биш байсан. Нэг хэсэг нь `Road`, нөгөө хэсэг нь `Frogger` дотор байсан.

### 4.4 Хэрэгжүүлсэн рефакторинг

Task 1-ийн хүрээнд responsibility-г `Road` class руу буцаан төвлөрүүлсэн.

Шинэ `Road` class:

```java
public class Road {
	private final boolean[] occupied;

	public boolean isValidPosition(int position) {
		return position >= 0 && position < occupied.length;
	}

	public boolean isOccupied(int position) {
		return occupied[position];
	}

	public boolean canMoveTo(int position) {
		return isValidPosition(position) && !isOccupied(position);
	}
}
```

Шинэ `Frogger.move()`:

```java
public boolean move(boolean forward) {
	int nextPosition = this.position + (forward ? 1 : -1);
	if (!this.road.canMoveTo(nextPosition)) {
		return false;
	}
	this.position = nextPosition;
	return true;
}
```

### 4.5 Энэ рефакторинг яг юу хийж байна вэ?

1. `Road.isValidPosition(int position)`
Замын өгөгдлийн хүрээнд тухайн байрлал хүчинтэй индекс мөн эсэхийг шалгана.

2. `Road.isOccupied(int position)`
Тухайн байрлалд саад эсвэл эзлэгдсэн төлөв байгаа эсэхийг шалгана.

3. `Road.canMoveTo(int position)`
Move хийх бизнес дүрмийг нэг method дотор нэгтгэнэ. Өөрөөр хэлбэл “энэ байрлал руу очиж болох уу?” гэсэн domain-level асуултад хариулж байна.

4. `Frogger.move(boolean forward)`
Одоо зөвхөн дараагийн байрлалыг тооцдог. Шийдвэрийг өөрөө гаргахгүй, `Road`-оос асуудаг болсон. Энэ нь `Tell, Don't Ask` зарчимд нийцэж байна.

5. `Frogger.isOccupied()` болон `Frogger.isValid()`
Эдгээр method нь өөрсдөө логик агуулж байхаа больж, `Road` руу delegate хийдэг болсон. Ингэснээр дотоод өгөгдөлд шууд хүрэхээ больсон.

### 4.6 Рефакторингийн үр дүн

1. `Road` нь замын төлөв болон замын дүрмийн жинхэнэ эзэн болсон.
2. `Frogger` нь илүү жижиг, ойлгомжтой class болсон.
3. Coupling буурч, cohesion нэмэгдсэн.
4. Дараа нь `Road`-ийн дотоод өгөгдлийн бүтэц өөрчлөгдсөн ч гаднах API тогтвортой байж чадна.

---

## 5. Даалгавар 2 - Frogger + Records

### 5.1 Рефактороос өмнөх кодын байдал

Рефактороос өмнө `Frogger` class нь өөрт хэт олон profile field хадгалж байсан.

```java
private final Records records;
private String firstName, lastName, phoneNumber, zipCode, state, gender;
```

Constructor нь олон параметртэй байсан.

```java
public Frogger(Road road, int position, Records records, String firstName,
			   String lastName, String phoneNumber, String zipCode,
			   String state, String gender)
```

Мөн `recordMyself()` method нь олон primitive утгыг шууд `Records` class руу дамжуулж байсан.

```java
public boolean recordMyself() {
	boolean success = records.addRecord(firstName, lastName, phoneNumber, zipCode, state, gender);
	return success;
}
```

`Records` class дотор өгөгдөл `String[]` байдлаар хадгалагдаж, давхардал шалгалт гар аргаар индексээр хийгдэж байсан.

```java
private final List<String[]> records;

for (String[] row : this.records) {
	if (row[0].equals(firstName)
			&& row[1].equals(lastName)
			&& row[2].equals(phoneNumber)
			&& row[3].equals(zipCode)
			&& row[4].equals(state)
			&& row[5].equals(gender)) {
		return false;
	}
}
```

### 5.2 Илэрсэн анти-паттерн ба design smell

| Ангилал | Хаана илэрсэн | Мэргэжлийн тайлбар |
| --- | --- | --- |
| Large Class | `Frogger` | Position-той холбоогүй profile data-г `Frogger` өөр дээрээ хэт их хадгалж байсан. |
| Long Parameter List | `Frogger` constructor, `Records.addRecord(...)` | Олон утга дараалалтай дамжиж байсан тул буруу дарааллаар дамжуулах эрсдэл өндөр байсан. |
| Data Clumps | `firstName`, `lastName`, `phoneNumber`, `zipCode`, `state`, `gender` | Хамтдаа нэг ойлголт илэрхийлдэг атлаа салангид primitive-үүдээр тараагдсан байсан. |
| Primitive Obsession | `String`-үүдээр identity илэрхийлсэн байдал | Domain ойлголт болох “frogger-ийн бүртгэлийн мэдээлэл” тусдаа type-гүй байсан. |
| Feature Envy | `Frogger.recordMyself()` | `Frogger` нь `Records`-ийн API-д олон утга нийлүүлж өгдөг дамжуулагч болж хувирсан. |
| Data structure smell | `List<String[]>` | Индекс дээр суурилсан өгөгдөл хадгалалт нь утгын утгыг бүдгэрүүлдэг, алдаа гарахад амархан. |

### 5.3 Хуучин кодоос үүсэх бодит асуудал

1. Параметрийн дараалал солигдвол код compile хийгдэнэ, гэхдээ буруу өгөгдөл бүртгэгдэнэ. Энэ нь compile-time safety муу байсныг харуулна.

2. `String[]`-ийн `row[0]`, `row[1]`, `row[2]` гэх мэт индексүүд нь ямар утга төлөөлж байгааг кодоос шууд ойлгоход төвөгтэй байсан.

3. Давхардал шалгах логик урт, давтагдсан, уншихад хэцүү байсан.

4. `Frogger` нь position-оос гадна хэрэглэгчийн profile data хадгалж байсан тул class-ийн cohesion буурсан.

### 5.4 Хэрэгжүүлсэн рефакторинг

Task 2-ийн хүрээнд `FroggerID` нэртэй record-ийг value object болгон ашигласан.

```java
public record FroggerID(String firstName,
						String lastName,
						String phoneNumber,
						String zipCode,
						String state,
						String gender) {
}
```

`Frogger` class дотор олон `String` field-ийн оронд нэг `FroggerID` field ашиглах болсон.

```java
private final Records records;
private final FroggerID froggerID;
```

Шинэ constructor:

```java
public Frogger(Road road, int position, Records records, FroggerID froggerID) {
	this.road = road;
	this.position = position;
	this.records = records;
	this.froggerID = froggerID;
}
```

Backward compatibility хадгалах үүднээс хуучин constructor-ийг устгалгүй, adapter хэлбэрт шилжүүлсэн.

```java
public Frogger(Road road, int position, Records records, String firstName,
			   String lastName, String phoneNumber, String zipCode,
			   String state, String gender) {
	this(road, position, records,
		 new FroggerID(firstName, lastName, phoneNumber, zipCode, state, gender));
}
```

`Records` class дотор `List<String[]>`-ийг `List<FroggerID>` болгож өөрчилсөн.

```java
private final List<FroggerID> records;

public boolean addRecord(FroggerID froggerID) {
	if (this.records.contains(froggerID)) {
		return false;
	}
	this.records.add(froggerID);
	return true;
}
```

Хуучин олон параметртэй API-г мөн compatibility зорилгоор хадгалсан.

```java
public boolean addRecord(String firstName, String lastName, String phoneNumber,
						 String zipCode, String state, String gender) {
	return addRecord(new FroggerID(firstName, lastName, phoneNumber, zipCode, state, gender));
}
```

### 5.5 Энэ рефакторинг яг юу хийж байна вэ?

1. `FroggerID`
Frogger-ийн бүртгэлийн мэдээллийг нэг бүхэл domain object болгон багцалж байна. Энэ нь “нэр, утас, zip, state, gender” гэх салангид утгуудыг нэг logical unit болгож өгдөг.

2. `Frogger` доторх `froggerID` field
`Frogger` өөрийн бүртгэлийн identity-г нэг объект дотор хадгална. Ингэснээр class-ийн бүтэц цэгцтэй болно.

3. `Frogger.recordMyself()`
Одоо `Records` class руу зургаан `String` дамжуулахгүй. Нэг `FroggerID` дамжуулдаг болсон. Энэ нь API-г илүү тодорхой, аюулгүй болгож байна.

4. `Records.addRecord(FroggerID froggerID)`
Энэ method нь шинэ бүртгэлийг list дотор байгаа эсэхээр шалгана. `FroggerID` нь record учраас `equals()` автоматаар бүх field-ээр зөв харьцуулагдана. Иймээс давхардал шалгах логик ихээхэн энгийн болсон.

5. `Records.addRecord(String ...)`
Энэ method нь хуучин API-г шууд эвдэхгүй байлгах transitional layer юм. Шинэ core design руу хөрвүүлэх adapter-ийн үүрэг гүйцэтгэж байна.

### 5.6 Рефакторингийн үр дүн

1. `Frogger` class-ийн responsibility багассан.
2. Constructor болон method signature-ууд цэгцэрсэн.
3. Domain model илүү ойлгомжтой болсон.
4. Давхардал шалгах логик богино, цэвэр, найдвартай болсон.
5. Primitive-үүдийн бөөгнөрөл (`data clump`) value object болж хувирсан.

---

## 6. Даалгавар 3 - drawing package (зөвхөн шинжилгээ)

Энэ даалгаврын хүрээнд кодон дээр өөрчлөлт хийгээгүй. Гэхдээ хуучин design дээрх асуудлыг мэргэжлийн түвшинд задлан шинжилсэн.

### 6.1 Илэрсэн асуудал 1 - Draw логикийн давхардал

`Drawing.draw()` method дотор `jpeg` болон `png` гэсэн хоёр branch бараг ижил кодтой байна.

```java
if (format.equals("jpeg")) {
	try (Writer writer = new JPEGWriter(filename + ".jpeg")) {
		for (Shape shape : this.shapes) {
			Line[] lines = shape.toLines();
			shape.draw(writer, lines);
		}
	}
} else if (format.equals("png")) {
	try (Writer writer = new PNGWriter(filename + ".png")) {
		for (Shape shape : this.shapes) {
			Line[] lines = shape.toLines();
			shape.draw(writer, lines);
		}
	}
}
```

Мөн `Shape.draw()` дотор writer-ийн concrete type дээр үндэслэн салаалж байна.

```java
if (writer instanceof JPEGWriter) {
	writer.write(line.toJPEG());
} else if (writer instanceof PNGWriter) {
	writer.write(line.toPNG());
}
```

### 6.2 Энэ хэсгийн анти-паттерн

| Ангилал | Хаана илэрсэн | Тайлбар |
| --- | --- | --- |
| Code Duplication | `Drawing.draw()` | JPEG болон PNG branch-ийн код бараг бүрэн ижил байна. |
| Shotgun Surgery | Шинэ формат нэмэх үед | `Drawing`, `Shape`, магадгүй `Line` зэрэг олон газарт зэрэг өөрчлөлт орно. |
| Type Checking | `Shape.draw()` | Behavior-ийг polymorphism биш, `instanceof`-оор сонгож байна. |
| Open/Closed Principle violation | Формат өргөтгөх үед | Шинэ төрөл нэмэх бүрт хуучин кодыг заавал өөрчилнө. |

### 6.3 Яагаад муу вэ?

1. Формат нэмэхэд олон class зэрэг өөрчлөгдөнө.
2. Кодын өөрчлөлт тархмал болсноор regression гарах эрсдэл нэмэгдэнэ.
3. Rendering логик interface default method дотор concrete writer type мэддэг болсон нь abstraction-ийг сулруулж байна.

### 6.4 Илэрсэн асуудал 2 - Drawing class хэт их responsibility авсан

`Drawing` class нь дараах олон responsibility-г зэрэг авч байна.

1. Формат сонгох
2. Writer үүсгэх
3. Shape iteration хийх
4. Shape-ийг line болгох workflow удирдах
5. Output rendering pipeline-г coordinate хийх

Энэ нь `God Class` болон `Low Cohesion` шинжтэй бүтэц юм.

### 6.5 Санал болгож буй рефакторингийн чиглэл

1. Polymorphism ашиглаж rendering format бүрийн behavior-ийг тусдаа abstraction руу салгах.
2. Writer үүсгэх логикийг factory class руу салгах.
3. `Shape.draw()`-ийг concrete writer type шалгадаг хэлбэрээс гаргаж, shape өөрөө abstraction-тай хамтран ажилладаг болгох.
4. `Drawing` class-ийг orchestration түвшинд хөнгөн болгох.

### 6.6 Хүлээгдэж буй архитектурын сайжруулалт

1. Шинэ file format нэмэх үед хуучин кодыг бага өөрчилнө.
2. Rendering pipeline өргөтгөхөд илүү амар болно.
3. Code duplication буурна.
4. Class-уудын responsibility илүү тодорхой болно.

---

## 7. Хуучин ба шинэ шийдлийн харьцуулалт

| Асуудал | Хуучин байдал | Шинэ байдал |
| --- | --- | --- |
| Замын дүрэм | `Frogger` өөрөө массив шалгадаг | `Road` өөрөө domain rule хариуцдаг |
| Замын өгөгдөл | `getOccupied()`-оор дотоод бүтэц ил гардаг | `Road` behavior-оороо дамжуулж ажилладаг |
| Frogger profile data | Олон `String` field | Нэг `FroggerID` value object |
| Record хадгалалт | `List<String[]>` | `List<FroggerID>` |
| Давхардал шалгалт | Индексээр гар аргаар харьцуулдаг | `contains()` + `FroggerID.equals()` |
| API readability | Урт parameter list | Богино, domain-oriented API |
| Drawing render logic | `if/else` + `instanceof` | Polymorphism руу шилжүүлэх шаардлагатай гэж дүгнэсэн |

---

## 8. Баталгаажуулалт

Frogger хэсгийн кодын рефакторингийн дараа Java module build хийж шалгахад compile error гараагүй. Энэ нь хийсэн өөрчлөлтүүд syntax болон build түвшинд зөв хэрэгжсэнийг баталж байна.

---

## 9. Дүгнэлт

Энэ лабораторийн гол үнэ цэнэ нь behavior өөрчлөхгүйгээр design сайжруулах дадлага байсан. Хуучин кодын гол асуудал нь responsibility буруу хуваарилагдсан, domain object-ууд өөрийн behavior-ийг эзэмшээгүй, primitive data хэт тарамдсан байдал байсан. Хэрэгжүүлсэн рефакторингийн үр дүнд:

1. `Frogger`, `Road`, `Records` class-уудын responsibility илүү тодорхой болсон.
2. Encapsulation болон cohesion сайжирсан.
3. API илүү ойлгомжтой, өргөтгөхөд хялбар болсон.
4. Drawing package дээр дараагийн шатны рефакторинг хийхэд тодорхой мэргэжлийн үндэслэл бэлтгэгдсэн.

Иймээс энэ ажлыг зөвхөн код цэвэрлэх ажил гэж үзэхгүй, харин domain model-ийг илүү зөв илэрхийлсэн, цаашдын maintenance зардлыг бууруулсан архитектурын сайжруулалт гэж дүгнэж болно.