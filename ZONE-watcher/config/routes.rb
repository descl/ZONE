ZONEWatcher::Application.routes.draw do
  resources :sources

  get "home/index"

  resources :items
  get "filters/getNumber"

  resources :filters
  
  root :to => "home#index"
  get "items/index", :action => "index", :controller => "items"
  #match 'items/:id/:filter' => 'items#show'
  match 'items/:id' => 'items#show', :constraints  =>  {:id => /[^\/]+/} 
  
end
