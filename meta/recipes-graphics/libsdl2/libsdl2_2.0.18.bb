SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

SECTION = "libs"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=095c2687a3c3908e26984eaa8ec2d770"

# arm-neon adds MIT license
LICENSE:append = " ${@bb.utils.contains('PACKAGECONFIG', 'arm-neon', '& MIT', '', d)}"
LIC_FILES_CHKSUM:append = " ${@bb.utils.contains('PACKAGECONFIG', 'arm-neon', 'file://src/video/arm/pixman-arm-neon-asm.h;md5=9a9cc1e51abbf1da58f4d9528ec9d49b;beginline=1;endline=24', '', d)}"

PROVIDES = "virtual/libsdl2"

SRC_URI = "http://www.libsdl.org/release/SDL2-${PV}.tar.gz \
           file://0001-Fix-build-against-wayland-1.20.patch \
"

S = "${WORKDIR}/SDL2-${PV}"

SRC_URI[sha256sum] = "94d40cd73dbfa10bb6eadfbc28f355992bb2d6ef6761ad9d4074eff95ee5711c"

inherit cmake lib_package binconfig-disabled pkgconfig

BINCONFIG = "${bindir}/sdl2-config"

CVE_PRODUCT = "simple_directmedia_layer sdl"

EXTRA_OECMAKE = "-DSDL_OSS=OFF -DSDL_ESD=OFF -DSDL_ARTS=OFF \
                 -DSDL_DISKAUDIO=OFF -DSDL_NAS=OFF -DSDL_ESD_SHARED=OFF \
                 -DSDL_DUMMYVIDEO=OFF \
                 -DSDL_RPI=OFF \
                 -DSDL_PTHREADS=ON \
                 -DSDL_RPATH=OFF \
                 -DSDL_SNDIO=OFF \
                "

# opengl packageconfig factored out to make it easy for distros
# and BSP layers to pick either (desktop) opengl, gles2, or no GL
PACKAGECONFIG_GL ?= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)}"

PACKAGECONFIG:class-native = "x11 ${PACKAGECONFIG_GL}"
PACKAGECONFIG:class-nativesdk = "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} ${PACKAGECONFIG_GL}"
PACKAGECONFIG ??= " \
    ${PACKAGECONFIG_GL} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa directfb pulseaudio x11', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland gles2', '', d)} \
    ${@bb.utils.contains("TUNE_FEATURES", "neon","arm-neon","",d)} \
"
PACKAGECONFIG[alsa]       = "-DSDL_ALSA=ON,-DSDL_ALSA=OFF,alsa-lib,"
PACKAGECONFIG[arm-neon]   = "-DSDL_ARMNEON=ON,-DSDL_ARMNEON=OFF"
PACKAGECONFIG[directfb]   = "-DSDL_DIRECTFB=ON,-DSDL_DIRECTFB=OFF,directfb,directfb"
PACKAGECONFIG[gles2]      = "-DSDL_OPENGLES=ON,-DSDL_OPENGLES=OFF,virtual/libgles2"
PACKAGECONFIG[jack]       = "-DSDL_JACK=ON,-DSDL_JACK=OFF,jack"
PACKAGECONFIG[kmsdrm]     = "-DSDL_KMSDRM=ON,-DSDL_KMSDRM=OFF,libdrm virtual/libgbm"
PACKAGECONFIG[opengl]     = "-DSDL_OPENGL=ON,-DSDL_OPENGL=OFF,virtual/libgl"
PACKAGECONFIG[pulseaudio] = "-DSDL_PULSEAUDIO=ON,-DSDL_PULSEAUDIO=OFF,pulseaudio"
PACKAGECONFIG[wayland]    = "-DSDL_WAYLAND=ON,-DSDL_WAYLAND=OFF,wayland-native wayland wayland-protocols libxkbcommon"
PACKAGECONFIG[x11]        = "-DSDL_X11=ON,-DSDL_X11=OFF,virtual/libx11 libxext libxrandr libxrender"

CFLAGS:append:class-native = " -DNO_SHARED_MEMORY"

BBCLASSEXTEND = "native nativesdk"
