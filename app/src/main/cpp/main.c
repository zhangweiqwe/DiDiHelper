#include <stdio.h>
#include <dlfcn.h>
#include "include/config.h"
#include "include/injector.h"
#include "include/utils.h"
#include "include/ptrace.h"
#include "include/elf_utils.h"

int main(int argc, char const *argv[]) {
    if (argc != 3) {
        printf("Usage: %s [process name] [library path]\n", argv[0]);
        return -1;
    }

    const char *process_name = argv[1];
    const char *library_path = argv[2];


    pid_t pid = GetPid(process_name);
    if (DEBUG) {
        printf("process name: %s, library path: %s, pid: %d\n", process_name, library_path, pid);
    }

    if (IsSelinuxEnabled()) {
        DisableSelinux();
        printf("IsSelinuxEnabled %d\n", IsSelinuxEnabled());
    }


    /*long so_handle = InjectLibrary(pid, library_path);
    PtraceAttach(pid);
    long hook_fuction_addr = CallDlsym(pid, so_handle, "my_printf");

    long params[1];
    params[0] = so_handle;
    params[1] = "11";
    if (DEBUG) {
        printf("dlclose called, hook_fuction_addr %lx process %d so handle %lx\n", hook_fuction_addr,
               pid, so_handle);
    }
    long z = CallRemoteFunction(pid, hook_fuction_addr, params, 2);
    PtraceDetach(pid);

    if (DEBUG) {
        printf("z = %lx\n", z);
    }*/


    long so_handle = InjectLibrary(pid, library_path);
    PtraceAttach(pid);
    long hook_fuction_addr = CallDlsym(pid, so_handle, "my_printf");
    long params[1];
    params[0] = 123;
    if (DEBUG) {
        printf("dlclose called, hook_fuction_addr %lx process %d so handle %lx\n", hook_fuction_addr,
               pid, so_handle);
    }
    long z = CallRemoteFunction(pid, hook_fuction_addr, params, 1);
    PtraceDetach(pid);

    if (DEBUG) {
        printf("z = %ld\n", z);
    }


/*
  long so_handle = InjectLibrary(pid, library_path);
    PtraceAttach(pid);
    long hook_fuction_addr = CallDlsym(pid, so_handle, "my_printf");
    PtraceDetach(pid);
    long original_function_addr = GetRemoteFuctionAddr(pid, LIBC_PATH, (long)printf);
    if (DEBUG) {
        printf("hook_fuction_addr: %lx, original_function_addr: %lx\n", hook_fuction_addr, original_function_addr);
    }

    PatchRemoteGot(pid,"/data/app/com.vixkw.pubgmhd.myapplication-Vg936qRTyzMWUmeyxi4ZcQ==/lib/arm64/libnative-lib.so", original_function_addr, hook_fuction_addr);

*/


    return 0;
}
