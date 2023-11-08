package ru.quipy.utils

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */

const val TEST_USERNAME = "test_username"
const val TEST_NICKNAME = "test_nickname"
const val TEST_PASSWORD = "test_password"
const val TEST_PROJECT_NAME = "test_project_name"
const val TEST_TAG_NAME = "test_tag_name"
const val TEST_TAG_COLOR = "test_tag_color"
const val TEST_TASK_TITLE = "test_task_title"

fun createTestProjectName(id: Int): String = "${TEST_PROJECT_NAME}_${id}"
fun createTestUsername(id: Int): String = "${TEST_USERNAME}_${id}"
fun createTestNickname(id: Int): String = "${TEST_NICKNAME}_${id}"
fun createTestPassword(id: Int): String = "${TEST_PASSWORD}_${id}"
fun createTestTagName(id: Int): String = "${TEST_TAG_NAME}_${id}"
fun createTestTagColor(id: Int): String = "${TEST_TAG_COLOR}_${id}"
fun createTestTaskTitle(id: Int): String = "${TEST_TASK_TITLE}_${id}"
