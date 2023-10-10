# Maintainer: Afonso Morais <moraisafonso@protonmail.com>
pkgname=seal-editor
pkgver=1.0
pkgrel=1
pkgdesc="A Simple Text Editor"
arch=('x86_64')
url="https://example.com/myapp"
license=('MIT')

# Source should contain the URL to download the JAR file
source=("https://github.com/Afonso-Morais/Seal-Editor/releases/download/SealEditor-JAR/SealEditor.jar")

sha256sums=('03fac2c96c6709bb08bc084a763a363ecba7c6fba5160ae24046d5d6901a8f76')


package() {
    # Create the necessary directories
    install -d "$pkgdir/usr/share/seal-editor"

    # Copy the JAR file to the package directory
    install -Dm644 "$srcdir/SealEditor.jar" "$pkgdir/usr/share/seal-editor/SealEditor.jar"
}