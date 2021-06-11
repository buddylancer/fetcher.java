<% 
    //(new Bula.Fetcher.Controller.Index(new Bula.Fetcher.Context()).execute(); 
    Bula.Fetcher.Context $context = new Bula.Fetcher.Context();
    Bula.Fetcher.Controller.Index $index = new Bula.Fetcher.Controller.Index($context);
    $index.execute();
%>