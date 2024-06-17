package com.sm.di

import com.sm.data.AuthDatasourceImpl
import com.sm.domain.interfaces.AuthDatasource
import org.koin.dsl.module

val dataSourceModule = module {

    single<AuthDatasource> {
        AuthDatasourceImpl(get())
    }
}