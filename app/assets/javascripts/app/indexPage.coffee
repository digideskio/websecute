require ["knockout", "/assets/javascripts/app/models/indexPageModel.js", "bootstrap"], (ko, IndexPageModel) ->

  model = new IndexPageModel
  ko.applyBindings(model)