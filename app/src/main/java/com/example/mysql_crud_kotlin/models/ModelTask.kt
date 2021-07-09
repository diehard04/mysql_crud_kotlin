package com.phjethva.mysql_db_crud_kotlin.models



class ModelTask {

    var id: Int = 0
    var taskName: String? = null
    var taskDateTime: String? = null

    constructor() {}

    constructor(taskName: String, taskDateTime: String) {
        this.taskName = taskName
        this.taskDateTime = taskDateTime
    }

    constructor(id: Int, taskName: String, taskDateTime: String) {
        this.id = id
        this.taskName = taskName
        this.taskDateTime = taskDateTime
    }

}