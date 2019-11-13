package com.example.todo_list

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class TodoListAdapter(realmResult: OrderedRealmCollection<Todo>) : RealmBaseAdapter<Todo>(realmResult) {

    //viewHolder 클래스는 전달받은 view에서 text1,text2 아이디를 가진 텍스트 뷰의 참조를 저장하는 역할을 한다.
    class ViewHolder(view:View){
        val dateTextView: TextView = view.findViewById(R.id.text1)
        val textTextView: TextView = view.findViewById(R.id.text2)
    }

    //아이템에 표시하는 뷰를 구성합니다.
    //position -> 리스트 뷰의 아이템 위치를 나타낸다.
    //convertView -> 재활용되는 아이템의 뷰이다.
    //parent -> 부모 뷰 즉 여기서는 리브트 뷰의 참조를 가리킨다.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh: ViewHolder
        val view: View

        if(convertView == null){
            //XML 레이아웃 파일을 읽어서 뷰로 반환한다.
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_todo,parent,false)
            vh = ViewHolder(view)
            //뷰 홀더 객체는 tag 프로퍼티로 view에 저장된다. tag 프로퍼티에는 Any형으로 어떠한 객체도 저장이 가능하다.
            view.tag  = vh
        } else{
            view = convertView
            vh = view.tag as ViewHolder
        }

        if(adapterData != null){
            val item = adapterData!![position]
            vh.textTextView.text = item.title
            vh.dateTextView.text = DateFormat.format("yyyy/MM/dd",item.date)
        }
        return view
    }

    override fun getItemId(position: Int): Long {
        if(adapterData != null){
            return adapterData!![position].id
        }
        return super.getItemId(position)
    }
}