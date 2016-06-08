function log_pdf(filename, pdf_filename)
    data = readlog(filename);
    width = data.time(end);
    height = max(data.speed) + 5;
    
    fig = subfig(2,1,1); % figure location and size, hit F1 for more info
    pspd = draw_speed(data);
    hold on;
    try
    pboxhit = draw_box_hits(data, 0.80*height);
    catch
        3;
    end
    try
    pedestrianhit = draw_pedestrian_hits(data, 0.90*height);
    catch
        3;
    end
    try
    pcog = draw_cognitive(data);
    catch
        3;
    end
     try
    pbox = draw_box_distractor(data, 0.30*height);
    catch
        3;
    end
    try
    ppedestrian = draw_pedestrian_distractor(data, 0.35*height);
    catch
        3;
    end
    try
    pcollect = draw_collect_distractor(data, 0.40*height);
    catch
        3;
    end
    try
    pdark = draw_dark_distractor(data, 0.45*height);
    catch
        3;
    end
     try
    psound = draw_sound_distractor(data, 0.50*height);
    catch
        3;
     end
     try
    ptext = draw_text_distractor(data, 0.55*height);
    catch
        3;
     end
     try
    pstraight = draw_straight(data, 0.25*height);
    catch
        3;
    end
    %draw_speed(data);
    
    
    hold off;
    
    axis([0, width, 0, height]);
    xlabel('Experiment time [s]');
    ylabel('Vehicle speed [km/h]');
    
    legend([pspd ; pcog ; pstraight ; pbox ; ppedestrian ; pcollect; pdark ; psound ; ptext ; pboxhit ; pedestrianhit ], ...
        'Speed', ...        
        'Cognitive load', ...
        'Road is straight', ...
        'Box distractor', ...
        'Pedestrian distractor', ...
        'Collect distractor', ...
        'Dark distractor', ...
        'Sound distractor', ...
        'Text distractor', ...
        'Box hit', ...
        'Pedestrian hit', ...
        'Location', 'northwest');
    
    fig2pdf(fig, pdf_filename, 'tightinset');
end
% RGB colors    pcollect ;         
% [0.50, 0.13, 0.13] dark brown        
% [0.95, 0.38, 0.00] dark orange - brownish
% [0.90, 0.67, 0.22] light brown
% [0.53, 1.00, 0.00] neon green
% [0.88, 1.00, 0.75] light green
% [0.11, 0.45, 0.16] dark green
% [0.00, 0.90, 0.84] hatsune miku color
% [0.14, 0.41, 0.55] dark blue - green
% [0.00, 0.11, 0.80] dark blue
% [0.80, 0.40, 0.77] violet
% [0.20, 0.15, 0.20] black
% [1.00, 0.00, 0.40] pink

% speed drawing

function p = draw_speed(data)
    p = plot(data.time, data.speed, '-', 'Color', [0.50, 0.13, 0.13], 'LineWidth', 1.5);
end

function p = draw_cognitive(data)
    p = plot(data.time, data.cognitive_load * 2, '-', 'Color', [0.00, 0.90, 0.84], 'LineWidth', 2.5);
end

% distractor drawing

function p = draw_box_distractor(data, height)
    p = draw_distractor(data, data.box_distraction, height, 'Color', [0.90, 0.67, 0.22], 'LineWidth', 6);
end

function p = draw_pedestrian_distractor(data, height)
    p = draw_distractor(data, data.pedestrian_distraction, height, 'Color', [0.00, 0.90, 0.84], 'LineWidth', 6);
end

function p = draw_dark_distractor(data, height)
    p = draw_distractor(data, data.dark_distraction, height, 'Color', [0.00, 0.11, 0.80], 'LineWidth', 6);
end

function p = draw_collect_distractor(data, height)
    p = draw_distractor(data, data.collect_distraction, height, 'Color', [1.00, 0.00, 0.40], 'LineWidth', 6);
end

function p = draw_sound_distractor(data, height)
    p = draw_distractor(data, data.sound_distraction, height, 'Color', [0.53, 1.00, 0.00], 'LineWidth', 6);
end

function p = draw_text_distractor(data, height)
    p = draw_distractor(data, data.text_distraction, height, 'Color', [0.111 0.099 0.088], 'LineWidth', 6);
end

function p = draw_straight(data, height)
    A = data.road_type == 0;
    p = draw_distractor(data, A, height, 'Color', [0.999 0.099 0.088], 'LineWidth', 6);
end

function p = draw_distractor(data, vals, height, varargin)
    diffs = [vals ; 0] - [0 ; vals];
    begins = data.time(diffs == 1)';
    ends = data.time(diffs(2:end) == -1)';
    N = numel(begins);
    assert(N == numel(ends));
    p = plot([begins ; ends], height*ones(2,N), varargin{:});
    p = p(1);
end

% hit drawing

function p = draw_box_hits(data, height)
    p = draw_hits(data, data.box_hits, height, 'x-', 'Color', [0.112 0.666 0.999], 'MarkerSize', 10, 'LineWidth', 1);
end

function p = draw_pedestrian_hits(data, height)
    p = draw_hits(data, data.pedestrian_hits, height, 'o-', 'Color', [0.80, 0.40, 0.77], 'MarkerSize', 10, 'LineWidth', 1);
end

function p = draw_hits(data, vals, height, varargin)
    diffs = [vals ; 0] - [0 ; vals];
    events = data.time(diffs > 0)';
    N = numel(events);
    p = plot([events ; events], diag([-100, height]) * ones(2,N), varargin{:});
end
