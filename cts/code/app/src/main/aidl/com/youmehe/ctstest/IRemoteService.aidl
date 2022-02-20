// IRemoteService.aidl
package com.youmehe.ctstest;

// Declare any non-default types here with import statements

interface IRemoteService {
    boolean run(int testId, int step, in Bundle args);
}