class Authentication < ActiveRecord::Base
  attr_accessible :create, :destroy, :index, :provider, :uid, :user_id
  belongs_to :user
end
