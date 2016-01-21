#
# The main entry point into the client side. Creates a new main page model and binds it to the page.
#
require.config {
  paths: {
    bootstrap: "./assets/lib/bootstrap/js/bootstrap"
    jquery: "./assets/lib/jquery/jquery"
    knockout: "./assets/lib/knockout/knockout"
  }
  shim: {
    bootstrap: {
      deps: ["jquery"],
      exports: "$"
    }
    jquery: {
      exports: "$"
    }
    knockout: {
      exports: "ko"
    }
  }
}
