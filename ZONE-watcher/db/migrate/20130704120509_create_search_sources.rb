class CreateSearchSources < ActiveRecord::Migration
  def change
    create_table :search_sources do |t|
      t.string :uri
      t.belongs_to :search
      t.timestamps
    end
  end
end
