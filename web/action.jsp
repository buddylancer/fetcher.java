<% 
    //(new Bula.Fetcher.Controller.Action(new Bula.Fetcher.Context()).execute();

    Bula.Fetcher.Context $context = new Bula.Fetcher.Context(request, response);
    Bula.Fetcher.Controller.Action $action = new Bula.Fetcher.Controller.Action($context);
    $action.execute();
%>