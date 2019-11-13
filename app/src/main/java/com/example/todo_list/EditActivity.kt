package com.example.todo_list

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.util.*

class EditActivity : AppCompatActivity() {

    //인스턴스를 얻는다.
    val realm = Realm.getDefaultInstance()
    val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val id = intent.getLongExtra("id",-1L)
        if(id == -1L) insertMode()
        else{
            updateMode(id)
        }

        calendarView.setOnDateChangeListener { _,year,month,dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        }

    }

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

    override fun onDestroy() {
        super.onDestroy()
        //인스턴스를 해제한다.
        realm.close()
    }
}
