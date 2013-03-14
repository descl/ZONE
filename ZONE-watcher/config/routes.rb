ZONEWatcher::Application.routes.draw do
  get "twitter/addtimeline", :action => "add_timeline", :controller => "twitter"
  get "twitter", :action => "index", :controller => "twitter"
  resources :sources

  #resources :authentications

  devise_for :users, :controllers => { :omniauth_callbacks => "users/omniauth_callbacks" }
  #match '/auth/:provider/callback' => 'authentications#create'

  resources :oldsources

  get "home/index"

  resources :items
  get "filters/getNumber"

  resources :filters

  root :to => "home#index"
  get "items/index", :action => "index", :controller => "items"
  #match 'items/:id/:filter' => 'items#show'
  match 'items/:id' => 'items#show', :constraints  =>  {:id =>  /.*/ }


end

