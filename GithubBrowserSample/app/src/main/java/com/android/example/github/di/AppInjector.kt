/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.github.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.android.example.github.GithubApp
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import github.di.Injectable

/**
 * Helper class to automatically inject fragments if they implement [Injectable].
 */
object AppInjector {
    fun <T : Comparable<T>> sort(list: List<T>): List<T> {

    }

    data class Point(val x: Int, val y: Int)

    operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun Point.minus(other: Point) = Point(x - other.x, y - other.y)

    fun main() {
        val point1 = Point(10, 20)
        val point2 = Point(20, 30)
        println(point1 + point2)
        println(point1 - point2)                   
    }

    fun fibonacci() = sequence {
        var params = Pair(0, 1)
        while (true) {
            yield(params.first)
            params = Pair(params.second, params.first + params.second)
        }
    }

    inline fun simple(x: Int): Int{
        return x * x
    }

    fun m() {
        for(count in 1..1000) {
            simple(count)
        }
    }

    class Empty

    fun init(githubApp: GithubApp) {
        DaggerAppComponent.builder().application(githubApp)
            .build().inject(githubApp)
        githubApp
            .registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    handleActivity(activity)
                }

                override fun onActivityStarted(activity: Activity) {

                }

                override fun onActivityResumed(activity: Activity) {

                }

                override fun onActivityPaused(activity: Activity) {

                }

                override fun onActivityStopped(activity: Activity) {

                }

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

                }

                override fun onActivityDestroyed(activity: Activity) {

                }
            })
    }

    private fun handleActivity(activity: Activity) {
        if (activity is HasSupportFragmentInjector || activity is Injectable) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(
                            fm: FragmentManager,
                            f: Fragment,
                            savedInstanceState: Bundle?
                        ) {
                            if (f is Injectable) {
                                AndroidSupportInjection.inject(f)
                            }
                        }
                    }, true
                )
        }
    }
}
