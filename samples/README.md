# Installation

Make sure to set two environment variables:

    $ export DYLD_LIBRARY_PATH="$YOUR_INFINITEGRAPH_INSTALL_DIR/lib"
    $ export OO_SERVER_LOG_DIR="SOME_LOG_DIR_WITH_PERMISSIONS"

Make sure to start your eclipse from the shell session you exported the variables in.

Don't forget to install the license file.
Either add the infinite graph lib folder to the build path, or change the dependencies paths of IG in the pom.xml file.