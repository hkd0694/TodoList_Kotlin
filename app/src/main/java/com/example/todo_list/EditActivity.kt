package com.example.todo_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
