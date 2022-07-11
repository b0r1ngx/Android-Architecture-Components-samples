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

import android.app.Application
import androidx.room.Room
import com.android.example.github.BuildConfig
import github.api.GithubAuthService
import github.db.GithubDb
import dagger.Module
import dagger.Provides
import github.api.GithubService
import github.apibuilder.ApiBuilder
import github.env.Env
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideGithubService(apiBuilder: ApiBuilder): GithubService =
        apiBuilder.provideGithubService(
            logLevel = HttpLoggingInterceptor.Level.BODY
        )

    @Singleton
    @Provides
    fun provideGithubAuthService(apiBuilder: ApiBuilder): GithubAuthService =
        apiBuilder.provideGithubAuthService()

    @Singleton
    @Provides
    fun provideDb(app: Application) = Room
        .databaseBuilder(app, GithubDb::class.java, "github.db")
        .fallbackToDestructiveMigration()
        .build()


    @Singleton
    @Provides
    fun provideUserDao(db: GithubDb) = db.userDao()

    @Singleton
    @Provides
    fun provideRepoDao(db: GithubDb) = db.repoDao()

    @Singleton
    @Provides
    fun provideEnv() =
        object : Env {
            override val GITHUB_CLIENT_ID: String
                get() = BuildConfig.GITHUB_CLIENT_ID

            override val GITHUB_CLIENT_SECRET: String
                get() = BuildConfig.GITHUB_CLIENT_SECRET
        }
}
