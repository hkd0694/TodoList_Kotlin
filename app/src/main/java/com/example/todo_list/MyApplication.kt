package com.example.todo_list

import android.app.Application
import io.realm.Realm

class MyApplication : Application() {

    //manifest에 <application 부분에 name으로 MyApplication을 넣어준다.
    //앱이 실행하면 가장 먼저 실행되는 애플리케이션 객체를 상속하여 Realm을 초기화 해야 한다..!!
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

}