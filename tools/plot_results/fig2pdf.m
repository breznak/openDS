function fig2pdf( fig, file, varargin )
%FIG2PDF  Print figure to pdf file.
%
%  fig2pdf( fig, file, ... )
%
%  Optional modifiers:
%    'tightfig'
%    'tightax'
%    'tightinset'
%    'loose'
%

% (c) 2008-05-28, Martin Matousek
% Last change: $Date:: 2009-03-05 20:23:25 +0100 #$
%              $Revision: 1 $

ax = findobj( fig, 'type', 'axes' );

fu = get( fig, 'units' );

set( fig, 'units', 'points' )
fpos = get( fig, 'pos' );

popt = {};

while( ~isempty( varargin ) )
  cmd = varargin{1};
  varargin = varargin(2:end);
  switch( cmd )
    case 'fontsize'
      fsiz = varargin{1};
      varargin = varargin(2:end);
      
      set( findall( fig, 'type', 'text' ), 'fontsize', fsiz );
      set( findall( fig, 'type', 'axes' ), 'fontsize', fsiz );
    case 'tightfig'
      if( numel( ax ) ~= 1 )
        error( 'There must be single axes for tightfig.' );
      end
        set( ax, 'units', 'normalized' );
        apos = get( ax, 'pos' );
        fpos = get( fig, 'pos' );
    
        fpos(3) = fpos(3) * apos(3);
        fpos(4) = fpos(4) * apos(4);
    
        set( fig, 'pos', fpos );
        set( ax, 'pos', [ 0 0 1 1] );
    case 'tightax'
      if( numel( ax ) ~= 1 )
        error( 'There must be single axes for tightax.' );
      end

      set( ax, 'pos', [ 0 0 1 1] );
    case 'tightinset'
      if( numel( ax ) ~= 1 )
        error( 'There must be single axes for tighinset.' );
      end

      u = get( ax, 'units' );
      set( ax, 'units', 'normalized' );
      for i = 1:10
        ti = get( ax, 'tightinset' );
        set( ax, 'pos', [ti(1:2) 1-ti(1:2)-ti(3:4)] );
      end
      set( ax, 'units', u );
    case 'loose'
      popt = [ popt { '-loose' } ]; %#ok
    case 'cmd'
      eval( varargin{1} )
      varargin = varargin(2:end);
    otherwise
      error( 'Wrong option ''%s''.', cmd );
  end 
end

set( fig, 'paperunits', 'points', 'papersize', fpos(3:4), ...
          'paperposition', [0 0 fpos(3:4)] );

if( ~isempty( file ) )
  hidden = [];
  for i = 1:length(ax)
    hidden = [ hidden; hide_strings_off_limits( ax(i) ) ];%#ok
  end
  
  print( fig, '-dpdf', popt{:}, file );
  fprintf( 'Saved: %s\n', file );
  
  if( ~isempty( hidden ) )
    set( hidden, 'visible', 'on' );
  end
end

set( fig, 'units', fu )


function hidden = hide_strings_off_limits( ax )
%

h = findobj( ax, 'type', 'text' );
xl = xlim(ax);
yl = ylim(ax);
zl = zlim(ax);

p = get( h, 'pos' );
s = get( h, 'string' );
v = get( h, 'visible' );
u = get( h, 'units' );

if( ~iscell( p ) )
  p = {p};
  s = {s};
  v = {v};
  u = {u};
end

hidden = [];
for i = 1:length(h)
  
  if( ( ~inside( p{i}(1), xl ) || ...
        ~inside( p{i}(2), yl ) || ...
        ~inside( p{i}(3), zl ) ) && strcmp( v{i}, 'on' ) && ...
      strcmp( u{i}, 'data' ) )
    set( h(i), 'visible', 'off' )
    hidden = [ hidden; h(i) ]; %#ok
    fprintf( 'Off-limits string temporarily hidden ''%s''\n', s{i} );
  end
end

function i = inside( val, lim )
  i = val >= lim(1) && val <= lim(2);
