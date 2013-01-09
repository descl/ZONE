ZONEWatcher::Application.routes.draw do
  #resources :authentications

  devise_for :users, :controllers => { :omniauth_callbacks => "users/omniauth_callbacks" }
  #match '/auth/:provider/callback' => 'authentications#create'

  resources :sources

  get "home/index"

  resources :items
  get "filters/getNumber"

  resources :filters
  
  root :to => "home#index"
  get "items/index", :action => "index", :controller => "items"
  #match 'items/:id/:filter' => 'items#show'
  match 'items/:id' => 'items#show', :constraints  =>  {:id =>  /.*/ } 
  

end
