android-layout-engine
=====================

[Experimental project] An engine for achieving granular control over the visual appearance and data persistence over views/widgets displayed in any mobile app.

Usage:

If you need to build a simple layout,

	

    JsonObject layout = new JsonObject(); // this layout is the layout sent from server
    LayoutBuilder builder = new DefaultLayoutBuilder().createSimpleLayoutBuilder(this);
    LayoutBuilderCallback callback = new LayoutBuilderCallback() {
	    @Override
    	public void onUnknownAttribute(ParserContext context, String attribute, final JsonElement element, final JsonObject object, View view) {
    	//                Log.d(TAG,"Unknown attribute "+attribute+" encountered for "+object);
    	//                if("onclick".equals(attribute))
    	//                {
    	//                    view.setOnClickListener(new View.OnClickListener() {
    	//                        @Override
    	//                        public void onClick(View v) {
    	//                           Toast.makeText(MainActivity.this,"View "+object+" clicked.",Toast.LENGTH_SHORT).show();
    	//                        }
    	//                    });
    	//                }
    	            }
    	
    	@Override
    	public View onUnknownViewType(ParserContext context, String viewType, JsonObject object, ViewGroup parent) {
    	
    	                //Log.e(TAG,"Unknown View "+viewType+" encountered. "+object);
    	                return null;
    	            }
    	        };
    	builder.setListener(callback);
    	View view = builder.build((ViewGroup)this.getWindow().getDecorView(),layout); 	// now you have a dynamic view which can be added to decorview


Builder types
=============
By default 3 types of layout builders are bundled.

 1. Simple
 2. DataParsing
 3. DataAndViewParsing

SimpleLayoutBuilder
-------------------
This is a layout builder which can parse json to construct an android view out of it. It uses the registered handlers to convert the json string to a view and then assign attributes. You can also assign a callback to get callbacks for unknown views and unknown attributes.

Example : 

    LayoutBuilder builder = new DefaultLayoutBuilderFactory().createSimpleLayoutBuilder(MainActivity.this);
	View view = builder.build((ViewGroup)MainActivity.this.getWindow().getDecorView(),layout);

DataParsingLayoutBuilder
------------------------
A layout builder built on top of simple layout builder which can additionally parse data blocks. What this does is that any attribute value starting with "$" as the prefix will be considered as a data block and will be retrieved from the Data Provider.

Example :

    LayoutBuilder builder = new DefaultLayoutBuilderFactory().createDataParsingLayoutBuilder(MainActivity.this, new GsonProvider(getResponse().getData()));
    builder.build((ViewGroup)MainActivity.this.getWindow().getDecorView(),layout);

DataAndViewParsingLayoutBuilder
------------------------
A layout builder built on top of data parsing layout builder which can make views reusable. What this means is that any view type which is not present in the built in list of views will be queried in the View Provider (third param in the constructor) and will be inserted accordingly. This is primarily useful when using a "childView" property combined with "children" being a data block which means that the children are dynamic and have to be fetched from data provider and the type of the every child will be specified by "childView" and fetched from view provider.

Example :

    LayoutBuilder builder = new DefaultLayoutBuilderFactory().createDataParsingLayoutBuilder(MainActivity.this, new GsonProvider(getResponse().getData()),new GsonProvider(getResponse().getViews()));
    builder.build((ViewGroup)MainActivity.this.getWindow().getDecorView(),layout);

