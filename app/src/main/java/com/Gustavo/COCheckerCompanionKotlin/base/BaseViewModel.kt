package com.Gustavo.COCheckerCompanionKotlin.base

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
abstract class BaseViewModel  @Inject constructor(): ViewModel() {
}