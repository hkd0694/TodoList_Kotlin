# Kotlin Study (9/9) - 2019/11/14

Kotlin을 공부하기 위해 간단한 앱부터 복잡한 앱까지 만들어 봄으로써 Kotlin에 대해 하기!

총 9개의 앱 중 아홉 번째 앱

프로젝트명 : Todo List

기능

* 할 일 목록을 표시한다.
  
* 할 일을 데이터베이스에 추가, 생성, 수정, 삭제한다.
  

핵심 구성 요소

* ListView : 목록을 표시하는 리브스트혀 뷰이다.
  
* Realm : 모바일용 데이터베이스이다.
  
라이브러리 설정

* Anko : 인텐트 다이얼로그, 고르 등을 구현하는 데 도움이 되는 라이브러리
  
* Realm : 객체 중심 저 메모리 모바일 데이터베이스

## Realm (모바일 데이터베이스)

안드로이드에서는 SQLite 데이터베이스를 지원하는데, SQLite는 강력하지만 다루기가 어렵고 코드양이 많지만, Realm은 적은 코드로도 데이터베이스를 작성할 수 있어 더욱 사용하기 쉽다.

1. Relam 데이터베이스 사용 준비
2. Realm 객체로 만드는 방법
3. Realm 모델 클래스 작성
4. Realm 초기화
5. 액티비티에서 Realm 인스턴스 객체 얻기
6. 할 일 추가,삭제,수정 처리
    

> #### Realm 데이터베이스 사용 준비

Realm을 사용하려면 먼저 프로젝트 수준의 build.gradle 파일을 열고 dependencies 항목에 추가해줘야한다. 

```kotlin

dependencies{
    ...
    classpath "io.realm:realm-gradle-plugin:5.2.0"
}

```

또한, 모듈 수준의 build.gradle 파일에 다음을 추가 해준다.

```kotlin

apply plugin: 'realm-android'

apply plugin: 'kotlin-kapt'

```

> #### Realm 객체로 만드는 방법

Realm에서 테이블로 사용하려면 모델 클래스 앞에 open을 붙이고 RealmObject 클래스를 상속받으면 된다.

```kotlin

open class Dog(val id: Long, 
                var title: String="",
                 var age:Int=0) : RealmObject() {

                 }

```

> #### Realm 모델 클래스 작성

Realm에서 테이블 정보를 다룰 Todo 모델 클래스를 새로 작성한다.

```kotlin

open class Todo(
    //@PrimaryKey -> 유일한 값이어야 하므로 기본키로 설정 해줘야 한다.
    @PrimaryKey var id: Long = 0,
    var title : String = "",
    var date : Long = 0
) :RealmObject() {
}

```

코틀린에서는 Realm에서 사용하는 클래스에 open 키워드를 추가해줘야 한다. 또한, id는 유일한 값이 되어야 하기 때문에 @PrimaryKey를 추가해줘야 한다. 기본키 제약은 Realm에서 제공하는 주석이며, 이 주석이 부여된 속성값은 중복을 허용하지 않는다.

> #### Realm 초기화

앱이 실행이 될 때 제일 먼저 Realm을 초기화하여 다른 액티비티에서 사용을 할 수 있도록 해야한다. 그럴러면 Application을 상속받아 manifest에 등록을 해주면 된다.

```kotlin

class MyApplication : Application() {

    //manifest에 <application 부분에 name으로 MyApplication을 넣어준다.
    //앱이 실행하면 가장 먼저 실행되는 애플리케이션 객체를 상속하여 Realm을 초기화 해야 한다..!!
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

}

-------------------------------------------------------------------
//AndroidManifest.xml
 <application
            android:name=".MyApplication"
            ...>

</application>

```

> ##### 액티비티에서 Realm 인스턴스 객체 얻기

```kotlin

class EditActivity : AppCompatActivity() {

    //인스턴스를 얻는다.
    val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

    }

    override fun onDestroy() {
        super.onDestroy()
        //인스턴스를 해제한다.
        realm.close()
    }

```

MyAppication 클래스에서 Realm 을 초기화 했다면 액티비티에서는 getDefaultInstance() 메서드를 이용하여 Realm 객체의 인스턴스를 얻을 수 있다.

>#### 할 일 추가,삭제,수정 처리

```kotlin

@SuppressLint("RestrictedApi")
    private fun insertMode(){

        //삭제 버튼 감추기
        deleteFab.visibility = View.GONE

        //완료 버튼을 클릭하면 추가
        doneFab.setOnClickListener {
            insertTodo()
        }
    }

    //수정 모드 초기화
    private fun updateMode(id: Long){


        //id에 해당하는 객체를 화면에 표시
        val todo = realm.where<Todo>().equalTo("id",id).findFirst()!!

        todoEditText.setText(todo.title)
        calendarView.date = todo.date

        //완료 버튼을 클릭하면 수정
        doneFab.setOnClickListener {
            updateTodo(id)
        }

        //삭제 버튼을 클릭하면 삭제
        deleteFab.setOnClickListener {
            deleteTodo(id)
        }

    }

    private fun insertTodo(){
        //트랜잭션이 시작된다.
        realm.beginTransaction()
        //createObject -> 새로운 Realm 객체를 생성
        val newItem = realm.createObject<Todo>(nextId())
        //값 설정
        newItem.title = todoEditText.text.toString()
        newItem.date = calendar.timeInMillis

        //트랜잭션 종료 반영
        realm.commitTransaction()

        alert("내용이 추가되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    private fun updateTodo(id: Long){
        realm.beginTransaction()

        //"id" 컬럼에 id 값이 있다면 findFirst() 메서드로 첫 번째 데이터를 반환한다.
        val updateItem = realm.where<Todo>().equalTo("id",id).findFirst()!!

        updateItem.title = todoEditText.text.toString()
        updateItem.date = calendar.timeInMillis

        realm.commitTransaction()

        alert("내용이 변경되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    private fun deleteTodo(id: Long){
        realm.beginTransaction()

        val deleteItem = realm.where<Todo>().equalTo("id",id).findFirst()!!

        deleteItem.deleteFromRealm()

        realm.commitTransaction()

        alert("내용이 삭제되었습니다.") {
            yesButton {
                finish()
            }
        }.show()
    }

    //다음 Id를 반환
    //Realm은 기본키 자동 기능을 지원하지 않기 때문에 id값중 최댓값에다 1을 더한 id값을 반환해준다.
    private fun nextId() : Int{
        //Todo :  테이블의 모든 값을 얻기위해 where<Todo>() 메서드 사용
        val maxId = realm.where<Todo>().max("id")
        //.max(fieldName:String) fildName 열 값 중 가장 큰 값을 Number형으로 반환한다.
        if(maxId != null){
            return maxId.toInt() + 1
        }
        return 0
    }

```

중요 메서드

* createObject<T: ReealmModel>(primaryKeyValue: Any?)
  
* realm.where<Todo>().equalTo("id",id).findFirst()!!
  
* max(fieldName:String)
  
* getLongExtra(name:String, defaultValue: Long)


## Kotlin Study List

1. [BmiCalculator](https://github.com/hkd0694/BmiCalc_Kotlin)
2. [StopWatch](https://github.com/hkd0694/StopWat_Kotlin)
3. [MyWebBrowser](https://github.com/hkd0694/MyWeb_Kotlin)
4. [TiltSensor](https://github.com/hkd0694/TSens_Kotlin)
5. [MyGallery](https://github.com/hkd0694/MGallery_Kotlin)
6. [GpsMap](https://github.com/hkd0694/GpsMap_Kotlin)
7. [Flashlight](https://github.com/hkd0694/FLight_Kotlin)
8. [Xylophone](https://github.com/hkd0694/Xyloph_Kotlin)
9. [Todo 리스트](https://github.com/hkd0694/TodoList_Kotlin)