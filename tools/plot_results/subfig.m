% SUBFIG - opens and resizes subfigures on the screen 
%
%                           
%   f = subfig(rows,cols,ord,f) opens the ord's figure of the rows x cols grid
%
%	rows = number of figure rows per screen
%	cols = number of figure column per screen
%	ord	 = order of figure (row oriented index), see SUBPLOT.
%	f	 = figure handle
%
%   f = subfig([left bottom height width],f) sets figure f to [...] position
%   
%   [left bottom height width] = figure position and size
%
%  See also FIGURE, SUBPLOT.

% T. Pajdla, pajdla@cmp.felk.cvut.cz
% 14 Feb 2011
function f = subfig(rows,cols,ord,f)

if nargin < 4
    f	 = figure('Visible','off');
end
 p = get(0,'userdata');
 if isfield(p,'screenposition');
     screen = getfield(p,'screenposition');
 else
     screen = get(0, 'ScreenSize');
 end 
 if nargin > 2
     sW     = screen(3);
     sH     = screen(4);
     fW     = sW/cols;
     fH     = sH/rows;
     i      = ceil(ord/cols);
     j      = rem(ord-1,cols);
     left   =      j * fW + screen(1);
     bottom = sH - i * fH + screen(2);
 else
     left   = rows(1)+screen(1);
     bottom = rows(2)+screen(2);
     fH     = rows(3);
     fW     = rows(4);
     f      = cols;
 end
 switch get(gcf,'menubar')
     case 'figure'
         fH = fH - 2*23;
 end
 fH = max(fH-23,1);
 set(f,'Position',[left bottom fW fH]);
 set(f,'Visible','on');
return
