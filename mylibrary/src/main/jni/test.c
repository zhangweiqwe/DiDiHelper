#include <stdio.h>
#include <dlfcn.h>
#include<unistd.h>

#define DLL_FILE_NAME "/data/local2/share.so"

int main()
{
    long long addr = 0;
    void (*func)(int);
    void *handle = dlopen(DLL_FILE_NAME, RTLD_NOW);
    if (handle == NULL)
    {
        fprintf(stderr, "Failed to open libaray %s error:%s\n", DLL_FILE_NAME, dlerror());
        return -1;
    }

    addr = (long long)dlsym(handle, "hookLoad");
    printf("%lld ",addr);

    addr = (long long)dlsym(handle, "hookLoad2");
    printf("%lld ",addr);


    func = addr;
    func(1);

    dlclose(handle);
    //sleep(-1);
    return 0;
}
