require [
  "knockout",
  "/assets/javascripts/app/models/hostsPageModel.js",
  "bootstrap"
], (ko, HostsPageModel) ->

  model = new HostsPageModel
  ko.applyBindings(model)