package com.gatchii.shared.common

import com.gatchii.domains.jwk.JwkRepository
import com.gatchii.domains.jwk.JwkService
import com.gatchii.domains.jwk.JwkServiceImpl
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Package: com.gatchii.shared.common
 * Created: Devonshin
 * Date: 01/12/2024
 */

class TaskHandlerTest {

    lateinit var jwkRepository: JwkRepository
    lateinit var jwkService: JwkService

    @BeforeEach
    fun setUp() {
        jwkRepository = mockk<JwkRepository>()
        jwkService = JwkServiceImpl(jwkRepository)
        TaskLeadHandler.addTasks(RepeatableTaskLeadHandler("repeatJwk", 10) {

        })
    }

    @AfterEach
    fun tearDown() {
        TaskLeadHandler.cleanTasks()
    }

    @Test
    fun `same name task should thrown exeption test`() {
        //given
        //when
        //then
        assertThrows<Exception> {
            TaskLeadHandler.addTasks(RepeatableTaskLeadHandler("repeatJwk", 10) {})
        }
    }


    @Test
    fun `leaseTaskHandlerInit test`() {
        //given
        //when
        val count = TaskLeadHandler.getTasks().count {
            it.taskName() == "jwk"
        }
        //then
        assert(TaskLeadHandler.getTasks().size == 2)
        assert(count == 1)
    }


    // Test to check if task is removed successfully when it exists
    @Test
    fun `test remove task successfully if exists`()
    {
        //given
        val taskName = "task1"
        val testTask = object : TaskLeadHandler() {
            override fun doTask() {}
            override fun taskName() = taskName
        }
        TaskLeadHandler.addTasks(testTask)

        //when
        TaskLeadHandler.removeTask(taskName)

        //then
        assertFalse(TaskLeadHandler.getTasks().any { it.taskName() == taskName })
    }

    // Test to check if nothing happens when trying to remove a non-existing task
    @Test
    fun `test do nothing when removing non existing task`()
    {
        //given
        val taskName = "nonExistingTask"
        val initialSize = TaskLeadHandler.getTasks().size

        //when
        TaskLeadHandler.removeTask(taskName)

        //then
        assertEquals(initialSize, TaskLeadHandler.getTasks().size)
    }

    // Test to ensure task name is removed from taskNameSet when task is removed
    @Test
    fun `test remove task name from set when task is removed`()
    {
        //given
        val taskName = "task2"
        val testTask = object : TaskLeadHandler() {
            override fun doTask() {}
            override fun taskName() = taskName
        }
        TaskLeadHandler.addTasks(testTask)
        //when
        TaskLeadHandler.removeTask(taskName)

        //then
        val count = TaskLeadHandler.getTasks().count {
            taskName == it.taskName()
        }
        assertFalse(count > 0)
    }

    // Test to ensure that removing a task that is not in the taskNameSet does not cause errors
    @Test
    fun `test safely remove task not in task name set without error`()
    {
        //given
        val taskName = "task3"
        TaskLeadHandler.getTaskNameSet().add(taskName)

        //when
        TaskLeadHandler.removeTask(taskName)

        //then
        assertFalse(TaskLeadHandler.getTaskNameSet().contains(taskName))
    }

}