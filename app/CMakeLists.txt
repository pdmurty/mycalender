cmake_minimum_required(VERSION 3.4.1)
add_library( # Specifies the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        src/main/cpp/Date.c
        src/main/cpp/swedate.c
        src/main/cpp/sweph.c
        src/main/cpp/swemplan.c
        src/main/cpp/swephlib.c
        src/main/cpp/swemmoon.c
        src/main/cpp/swejpl.c
        src/main/cpp/swehouse.c
        src/main/cpp/swecl.c

        )

 find_library( # Defines the name of the path variable that stores the
               # location of the NDK library.
               log-lib

               # Specifies the name of the NDK library that
               # CMake needs to locate.
               log )

 # Links your native library against one or more other native libraries.
 target_link_libraries( # Specifies the target library.
                        native-lib

                        # Links the log library to the target library.
                        ${log-lib} )