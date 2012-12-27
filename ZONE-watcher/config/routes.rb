ZONEWatcher::Application.routes.draw do
  get "home/index"

  resources :items
  get "filters/getNumber"

  resources :filters
  
  root :to => "home#index"
  
  #match 'items/:id/:filter' => 'items#show'
  match 'items/:id' => 'items#show', :constraints  =>  {:id => /[^\/]+/} 
  
end
