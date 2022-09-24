package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import MainCoroutineScopeRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.UserEntity
import com.cleanarchitectkotlinflowhiltsimplestway.data.remote.Api
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.UserRepositoryImpl
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.User
import com.cleanarchitectkotlinflowhiltsimplestway.domain.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetUserListTest {
  @Mock
  private lateinit var api: Api
  private lateinit var userRepository: UserRepository
  private lateinit var getUserList: GetUserList

  @get:Rule
  val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  val coroutineScope = MainCoroutineScopeRule()

  @Before
  fun setup() {
    userRepository = UserRepositoryImpl(api) //Mockito.mock(UserRepository::class.java)
    getUserList = GetUserList(userRepository)
  }

  @Test
   fun successCase() {
    val data = mutableListOf<User>()
    data.add(User(""))
    data.add(User(""))

    runTest {
      print("Start")
      whenever(api.getUsers()).thenReturn(provideMockUserEntities())
      //Mockito.doReturn(provideMockUserEntities()).`when`(api.getUsers())
      Assert.assertEquals(userRepository.getUser().size == 1, true)
      Assert.assertEquals(userRepository.getUser().isNotEmpty(), true)


      val data = userRepository.getUser()
      Assert.assertEquals(data.first().name == "a", true)

      val flow = flow {
        whenever(api.getUsers()).thenReturn(provideMockUserEntities())
        emit(userRepository.getUser())
      }.toList()
      Assert.assertEquals(flow.first().first().name == "a", true)


    /*  val flow2 = flow {
        whenever(api.getUsers()).thenReturn(provideMockUserEntities())
        val data = getUserList.invoke(Unit).last()
        Assert.assertEquals(data is State.DataState, true)
        emit("")
      }.toList()*/


        whenever(api.getUsers()).thenReturn(provideMockUserEntities())
        val l = getUserList.invoke(Unit)
          .flowOn(TestCoroutineDispatcher())
          .toList()
        val data1 = l[1]
        Assert.assertEquals(data1 is State.DataState && data1.data.isNotEmpty(), true)


    }



  }

  private fun provideMockUserEntities(): List<UserEntity> {
    return mutableListOf(UserEntity("a"))
  }
}