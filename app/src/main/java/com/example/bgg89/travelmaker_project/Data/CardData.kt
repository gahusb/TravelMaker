package com.example.bgg89.travelmaker_project.Data

import com.example.bgg89.travelmaker_project.R
import com.ramotion.expandingcollection.ECCardData
import java.util.ArrayList
import java.util.Arrays

class CardData(val cardTitle : String,
               private val mainBackgroundResource : Int?,
               private val headBackgroundResource : Int?,
               private val listItems : List<String>,
               val travelNumber : Int
) : ECCardData<String> {
    override fun getMainBackgroundResource(): Int? {
        return mainBackgroundResource
    }

    override fun getHeadBackgroundResource(): Int? {
        return headBackgroundResource
    }

    override fun getListItems(): List<String> {
        return listItems
    }

    companion object {
        fun generateExampleData(): List<ECCardData<*>>{
            var list = ArrayList<ECCardData<*>>()
//            list.add(
//                    CardData(
//                            "Card 1",
//                            R.drawable.aud,
//                            R.drawable.aud_head,
//                            createItemList("Card 1"),
//                            1
//                    )
//            )
//            list.add(
//                    CardData(
//                            "Card 2",
//                            R.drawable.bgn,
//                            R.drawable.bgn_head,
//                            createItemList("Card 2"),
//                            2
//                    )
//            )
//            list.add(
//                    CardData(
//                            "Card 3",
//                            R.drawable.brl,
//                            R.drawable.brl_head,
//                            createItemList("Card 3"),
//                            3
//                    )
//            )
            return list
        }

        private fun createItemList(cardName : String) : List<String>{
            var list = ArrayList<String>()
            list.addAll(
                    Arrays.asList(
                            "$cardName - Item 1",
                            "$cardName - Item 2",
                            "$cardName - Item 3",
                            "$cardName - Item 4",
                            "$cardName - Item 5",
                            "$cardName - Item 6",
                            "$cardName - Item 7"
                    )
            )
            return list
        }
    }
}
