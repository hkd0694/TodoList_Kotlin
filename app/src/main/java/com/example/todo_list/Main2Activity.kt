package com.example.todo_list

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import org.jetbrains.anko.startActivity

class Main2Activity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        //sort() -> 날짜순으로 내림차순 정렬하여 얻습니다.
        val resultResult = realm.where<Todo>().findAll().sort("date", Sort.DESCENDING)

        val adapter = TodoListAdapter(resultResult)
        listView.adapter = adapter

        //데이터가 변경되면 어댑터에 적용시킨다.
        resultResult.addChangeListener { _ -> adapter.notifyDataSetChanged() }
        
        
        listView.setOnItemClickListener { _ ,_ ,_ ,id ->
            //할 일 수정
            startActivity<EditActivity>("id" to id)
        }

        fab.setOnClickListener {
            startActivity<EditActivity>()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}
