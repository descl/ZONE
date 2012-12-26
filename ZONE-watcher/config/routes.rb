ZONEWatcher::Application.routes.draw do
  get "home/index"

  resources :items

  resources :filters
  
  root :to => "home#index"
  
  match 'items/:id' => 'items#show', :constraints => {:id => /.*/}
end
