package com.example.todo_list

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Todo(
    //@PrimaryKey -> 유일한 값이어야 하므로 기본키로 설정 해줘야 한다.
    @PrimaryKey var id: Long = 0,
    var title : String = "",
    var date : Long = 0
) :RealmObject() {
}