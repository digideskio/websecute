require ["knockout", "./assets/javascripts/app/models/indexPageModel", "bootstrap"], (ko, IndexPageModel) ->

  model = new IndexPageModel
  ko.applyBindings(model)