package com.smoothapp.notionshortcut.model.entity

import com.smoothapp.notionshortcut.model.constant.NotionColorEnum

object NotionApiPostPageObj {

    fun parentInDatabase(dbId: String) = """
        "parent": {
            "database_id": "$dbId" 
        }
    """.trimIndent()

    fun parentInPage(){

    }

    fun propertyTitle(name: String, title: String) = """
        "$name": {
            "title": [
                {
                    "text": {
                        "content": "$title"
                    }
                }
            ]
        }
    """.trimIndent()

    fun propertyRichText(name: String, content: String) = """
        "$name": {
            "rich_text": [
                {
                    "text": {
                        "content": "$content"
                    }
                }
            ]
        }
    """.trimIndent()

    fun propertyNumber(name: String, number: String) = """
        "$name": {
            "number": $number
        }
    """.trimIndent()


    fun propertyCheckbox(name: String, checked: Boolean) = """
        "$name": {
            "checkbox": $checked
        }
    """.trimIndent()



    fun propertySelect(name: String, option: NotionOption): String{
        var result = """
            "$name": {
                "select": {
                    "name": "${option.name}"
        """
        /* todo: ここにcolorの判定は不要？ */
        if(option.color != null){
            result += """ ,"color": "${option.color.getName()}"  """
        }
        result += "}}"

        return result.trimIndent()
    }

    fun propertyMultiSelect(name: String, options: List<NotionOption>): String{
        var result = """
            "$name": {
                "multi_select": [
        """

        for(opt in options){
            result += """
                {
                    "name": "${opt.name}"
            """
            /* todo: ここにcolorの判定は不要？ */
            if(opt.color != null){
                result += """ ,"color": "${opt.color.getName()}" """
            }
            result += "},"
        }

        result = result.dropLast(1) + "]}"

        return result
    }

    //todo: 要素追加時の引数(group, color)
    fun propertyStatus(name: String, option: NotionOption) = """
        "$name": {
            "status": {
                "name": "${option.name}"
            }
        }
    """.trimIndent()

    /*todo: relation has more への対応*/
    fun propertyRelation(name: String, options: List<NotionOption>): String{
        var result = """
            "$name": {
                "relation": [
        """

        for(option in options){
            result += """
                {
                    "id": "${option.id}"
                }
            """.trimIndent()

            result += ","
        }

        result = result.dropLast(1) + "]}"

        return result
    }


    fun propertyDate(name: String, fromDate: NotionDateTime, toDate: NotionDateTime?): String{
        var result = """
            "$name": {
                "date": {
                    "start": "${fromDate.convertToString()}"
        """

        if(toDate != null){
            result += """
                    ,
                    "end": "${toDate.convertToString()}"
            """
        }

        result += "}}"

        return result
    }


}